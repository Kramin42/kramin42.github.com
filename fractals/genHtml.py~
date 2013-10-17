
n=3
startN=20
width=1024
height=768

for i in range(0,n):
    filename="fractal"+str(i+startN)+".htm"
    file=open(filename,'w')
    file.write("<html><head><title>Fractal</title></head><body><img border=\"0\" src=\"fractal"+str(i+startN)+".png\" width="+str(width)+" height="+str(height)+" alt=\"Fractal\" /></body></html>")
    file.close()

print("put this in the Gallery.html file:")
for i in range(0,n):
    print("<div class=\"img\"><a target=\"_blank\" href=\"./fractals/fractal"+str(i+startN)+".htm\"><img src=\"./fractals/fractal"+str(i+startN)+"_small.png\" alt=\"800x600\" width=\"110\" height=\"90\" /></a><div class=\"desc\">size: "+str(width)+"x"+str(height)+"</div></div>")
