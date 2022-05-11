#!/usr/bin/env bash
git pull origin master
git tag -f leda
git push -f origin leda