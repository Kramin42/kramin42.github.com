
n=1
startN=8
width=1920
height=1080

for i in range(0,n):
    filename="desktop"+str(i+startN)+".htm"
    file=open(filename,'w')
    file.write("<html><head><title>Fractal</title></head><body><img border=\"0\" src=\"fractal"+str(i+startN)+".png\" width="+str(width)+" height="+str(height)+" alt=\"Fractal\" /></body></html>")
    file.close()

print("put this in the Gallery.html file:")
for i in range(0,n):
    print("<div class=\"img\"><a target=\"_blank\" href=\"./fractals/desktop"+str(i+startN)+".htm\"><img src=\"./fractals/desktop"+str(i+startN)+"_small.png\" alt=\"800x600\" width=\"110\" height=\"90\" /></a><div class=\"desc\">size: "+str(width)+"x"+str(height)+"</div></div>")
