#!/usr/bin/env bash
set -Eeuo pipefail

kind="${1:?Pass ktor or spring}"
digest="${2:?Pass the published image digest}"
[[ "$kind" == ktor || "$kind" == spring ]] || { echo 'Expected ktor or spring' >&2; exit 1; }
[[ "$digest" =~ ^sha256:[0-9a-f]{64}$ ]] || { echo 'Expected a sha256 image digest' >&2; exit 1; }

exec 9>/run/lock/server-deploy.lock
flock -n 9 || { echo 'A server deployment is already running' >&2; exit 1; }

cd "$(dirname "${BASH_SOURCE[0]}")"
compose_project=server
server_container="${compose_project}-server-1"
db_container="${compose_project}-db-1"
ktor_repo=ghcr.io/pvlbndrnk/kmp-websocket-chat-server-ktor
spring_repo=ghcr.io/pvlbndrnk/kmp-websocket-chat-server-spring

compose() {
  local server_kind="$1"
  shift
  docker compose -p "$compose_project" --env-file "${server_kind}.env" -f "compose.${server_kind}.yaml" "$@"
}

case "$kind" in
  ktor) repo="$ktor_repo" ;;
  spring) repo="$spring_repo" ;;
esac
image="${repo}@${digest}"

active_image="$(docker inspect --format '{{.Config.Image}}' "$server_container" 2>/dev/null || true)"
case "$active_image" in
  "$ktor_repo"@*|"$ktor_repo":*) active_kind=ktor ;;
  "$spring_repo"@*|"$spring_repo":*) active_kind=spring ;;
  '') active_kind='' ;;
  *) echo "Unknown active server image: $active_image" >&2; exit 1 ;;
esac

healthy_image() {
  local expected_image="$1"
  for attempt in 1 2 3 4 5 6 7 8 9 10; do
    if [[ "$(docker inspect --format '{{.Config.Image}}' "$server_container" 2>/dev/null || true)" == "$expected_image" ]] &&
       curl -fsS -o /dev/null --max-time 5 --noproxy '*' \
         --resolve pvlbndrnk.duckdns.org:443:127.0.0.1 \
         https://pvlbndrnk.duckdns.org/messages; then
      return 0
    fi
    sleep 3
  done
  return 1
}

restore_active_server() {
  if [[ -z "$active_kind" ]]; then
    compose "$kind" stop server caddy
    return
  fi
  export SERVER_IMAGE="$active_image"
  if [[ "$active_kind" == spring ]]; then
    compose spring up -d --wait --wait-timeout 120 db || return 1
  fi
  compose "$active_kind" up -d --no-deps --force-recreate server caddy &&
    healthy_image "$active_image"
}

test -f "${kind}.env"
if [[ $(grep -c '^SERVER_IMAGE=' "${kind}.env" || true) != 1 ]]; then
  echo "Expected exactly one SERVER_IMAGE entry in ${kind}.env" >&2
  exit 1
fi
export SERVER_IMAGE="$image"
compose "$kind" config --quiet
docker pull "$image"

if [[ "$kind" == spring ]]; then
  if ! compose spring up -d --wait --wait-timeout 120 db; then
    echo 'PostgreSQL did not become healthy; keeping the active server' >&2
    [[ "$active_kind" == spring ]] || docker stop "$db_container" >/dev/null 2>&1 || true
    exit 1
  fi
fi

if ! compose "$kind" up -d --no-deps --force-recreate server caddy ||
   ! healthy_image "$image"; then
  echo 'Deployment failed; restoring the previous server' >&2
  if ! restore_active_server; then
    echo 'Rollback failed; inspect the server and Caddy containers' >&2
  fi
  if [[ "$active_kind" != spring ]]; then
    docker stop "$db_container" >/dev/null 2>&1 || true
  fi
  exit 1
fi

if [[ "$kind" == ktor ]]; then
  # Keep PostgreSQL's named volume for a later Spring deployment.
  docker stop "$db_container" >/dev/null 2>&1 || true
fi
sed -i "s|^SERVER_IMAGE=.*|SERVER_IMAGE=${image}|" "${kind}.env"
echo "Deployment succeeded: $kind $image"
