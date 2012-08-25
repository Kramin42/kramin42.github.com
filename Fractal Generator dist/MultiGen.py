import os

n=0
startN=18
width=1024
height=768
mult=1

file = open('MultiGenArgs.txt','r')

for line in file:
    if (line=="\n"): continue
    line=line.strip()
    os.system("Fractal_saver.exe "+line+" -IW "+str(mult*width)+" -IH "+str(mult*height))
    os.system("convert fractal.bmp -resize "+str(width)+"x"+str(height)+" fractal"+str(n+startN)+".png")
    n+=1

file.close()
