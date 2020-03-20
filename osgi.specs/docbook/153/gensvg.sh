#!/bin/sh
#dot -Tpng dto.dot -o onem2m-dto.png
dot -Tsvg dto.dot -o temp.svg

sed 's/transparent/white/' temp.svg > onem2m-dto.svg
rm ./temp.svg
