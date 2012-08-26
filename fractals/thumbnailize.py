import os
startN=20
n=3

for i in range(0,n):
    os.system("convert fractal"+str(i+startN)+".png -resize 110x90^^ -gravity center -extent 110x90 fractal"+str(i+startN)+"_small.png")

