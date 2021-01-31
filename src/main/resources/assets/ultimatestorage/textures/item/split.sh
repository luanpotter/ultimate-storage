#!/bin/bash -e

convert items.png -crop 4x4@ out-%d.png

rm -rf gen
mkdir gen

mv out-0.png gen/base-upgrade.png
mv out-1.png gen/wrench.png
mv out-2.png gen/capacity-upgrade.png
mv out-3.png gen/filter-upgrade.png

mv out-4.png gen/wood-tier-upgrade.png
mv out-5.png gen/iron-tier-upgrade.png
mv out-6.png gen/gold-tier-upgrade.png
mv out-7.png gen/diamond-tier-upgrade.png

rm out-*
