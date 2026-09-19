#!/usr/bin/env bash
# Shell script to verify repository size is strictly under 10 MB limit
set -e

echo "=== NyayaLens Repository Size Audit ==="

MAX_MB=10
REPO_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"

# Calculate source files excluding ignored patterns
TOTAL_BYTES=$(find "$REPO_DIR" -type f \
  -not -path "*/node_modules/*" \
  -not -path "*/target/*" \
  -not -path "*/.git/*" \
  -not -path "*/dist/*" \
  -not -path "*/build/*" \
  -exec ls -dn {} + | awk '{total += $5} END {print total}')

TOTAL_MB=$(awk "BEGIN {printf \"%.2f\", $TOTAL_BYTES / 1048576}")
echo "Source Tree Size: ${TOTAL_MB} MB"

if (( $(echo "$TOTAL_MB < $MAX_MB" | bc -l) )); then
  echo "SUCCESS: Repository size is within ${MAX_MB} MB limit."
  exit 0
else
  echo "ERROR: Repository exceeds ${MAX_MB} MB limit!"
  exit 1
fi
