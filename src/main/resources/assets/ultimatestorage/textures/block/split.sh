#!/bin/bash -e

convert blocks.png -crop 4x4@ out-%d.png

rm -rf gen
mkdir gen

i=0
for material in wood iron gold diamond; do
  for side in front side top bottom; do
    mv "out-$i.png" "gen/$material-$side.png"
    i=$((i+1))
  done
done
