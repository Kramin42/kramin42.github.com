set in="desktop7.png"
set out="desktop7_small.png"
convert %in% -resize 160x90^ -gravity center -extent 110x90 %out%