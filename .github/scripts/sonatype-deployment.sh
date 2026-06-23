#!/usr/bin/env bash
# -------------------------------------------------------------------------
# sonatype-deployment.sh – Control a staged Sonatype Central Portal
# deployment: status | publish | drop.
#
# Complements sonatype-upload.sh / sonatype-status.sh: once a deployment has
# been uploaded as USER_MANAGED (and ended at VALIDATED), this script drives
# the deliberate publish-or-drop decision.
#
# API (https://central.sonatype.org/publish/publish-portal-api/):
#   status : POST   /api/v1/publisher/status?id={id}
#   publish: POST   /api/v1/publisher/deployment/{id}   VALIDATED -> PUBLISHING (HTTP 204)
#   drop   : DELETE /api/v1/publisher/deployment/{id}   removes VALIDATED/FAILED (HTTP 204)
#
# Usage:
#   SONATYPE_BEARER=<token> ./.github/scripts/sonatype-deployment.sh \
#     [--status|--publish|--drop] [--id] <deployment-id>
#
# Environment:
#   SONATYPE_BEARER   – Bearer token for authentication (required)
# -------------------------------------------------------------------------
set -euo pipefail
API="https://central.sonatype.com/api/v1/publisher"
ACTION="status"; DEPLOYMENT_ID=""
while [[ $# -gt 0 ]]; do
  case "$1" in
    --status)  ACTION="status";  shift ;;
    --publish) ACTION="publish"; shift ;;   # VALIDATED -> PUBLISHING -> PUBLISHED
    --drop)    ACTION="drop";    shift ;;   # delete a not-yet-published deployment
    --id)      DEPLOYMENT_ID="$2"; shift 2 ;;
    *)         DEPLOYMENT_ID="$1"; shift ;;
  esac
done
: "${SONATYPE_BEARER:?SONATYPE_BEARER not set}"
: "${DEPLOYMENT_ID:?deployment id required}"
AUTH=(-H "Authorization: Bearer ${SONATYPE_BEARER}")
case "$ACTION" in
  status)  curl -fsS -X POST   "${AUTH[@]}" "${API}/status?id=${DEPLOYMENT_ID}"; echo ;;
  publish) curl -fsS -X POST   "${AUTH[@]}" "${API}/deployment/${DEPLOYMENT_ID}"; echo "published ${DEPLOYMENT_ID}" ;;
  drop)    curl -fsS -X DELETE "${AUTH[@]}" "${API}/deployment/${DEPLOYMENT_ID}"; echo "dropped ${DEPLOYMENT_ID}" ;;
esac
