#!/usr/bin/env bash
echo "adding all files in this folder (recursive) out of quarantine..."
xattr -r -d com.apple.quarantine *
echo "done!"