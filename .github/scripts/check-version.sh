#!/usr/bin/env bash
set -euo pipefail

VERSION=$(grep 'packageVersion' composeApp/build.gradle.kts | sed -E 's/.*packageVersion = "(.*)".*/\1/')

if git ls-remote --tags origin | grep -q "refs/tags/v${VERSION}$"; then
  TAG_EXISTS=true
else
  TAG_EXISTS=false
fi

LATEST_TAG=$(git ls-remote --tags origin | awk '{print $2}' | sed 's#refs/tags/##' | grep -E '^v[0-9]+\.[0-9]+\.[0-9]+$' | sed 's/^v//' | sort -V | tail -n1)

if [ -z "$LATEST_TAG" ]; then
  VERSION_OK=true
else
  HIGHEST=$(printf '%s\n%s\n' "$LATEST_TAG" "$VERSION" | sort -V | tail -n1)
  if [ "$HIGHEST" == "$VERSION" ] && [ "$VERSION" != "$LATEST_TAG" ]; then
    VERSION_OK=true
  else
    VERSION_OK=false
  fi
fi

{
  echo "version=$VERSION"
  echo "tag_exists=$TAG_EXISTS"
  echo "version_ok=$VERSION_OK"
} >> "$GITHUB_OUTPUT"
