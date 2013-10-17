import os
startN=8
n=1

for i in range(0,n):
    os.system("convert desktop"+str(i+startN)+".png -resize 110x90^^ -gravity center -extent 110x90 desktop"+str(i+startN)+"_small.png")

