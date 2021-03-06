// A bouncing ball/Brownian motion sim

int w=400, h=400;
float g = 0.0;//set to zero for brownian motion
float startSpeed = 1.0;
int numStartBalls = 2000;
ArrayList<Ball> balls;
void setup() {
  frameRate(60);
  size(400,400);
  noStroke();
  background(0);
  ellipseMode(RADIUS);
  
  balls = new ArrayList<Ball>();
  for (int i=0; i < numStartBalls; i++){
    Ball a = new Ball(50+random(w-100), 50+random(h-100), random(-startSpeed,startSpeed), random(-startSpeed,startSpeed));
    a.rad = 1;
    balls.add(a);
  }
} 

void draw() {
  //background(255,255,255,10);
  fill(0,0,0,100);
  rect(0,0,w,h);
  //resolve collisions
  for (int i=0; i < balls.size(); i++){
    Ball a = balls.get(i);
    for (int j=i+1; j < balls.size(); j++){
      Ball b = balls.get(j);
      float d = a.pos.dist(b.pos);
      if (d < a.rad + b.rad){
        //println("a pos: "+a.pos+", b pos: "+b.pos);
        PVector Dn = PVector.sub(a.pos,b.pos);
        Dn.mult(1/d);
        float M = a.mass + b.mass;
        PVector mT = PVector.mult(Dn, a.rad + b.rad - d);
        //println(d);
        a.pos.add(PVector.mult(mT,b.mass/M));
        b.pos.add(PVector.mult(mT,-a.mass/M));
        
        PVector van = PVector.mult(Dn, PVector.dot(a.vel,Dn));
        PVector vbn = PVector.mult(Dn, PVector.dot(b.vel,Dn));
        
        float vanl = van.mag();
        float vbnl = vbn.mag();
        
        a.vel.add(PVector.mult(Dn,((b.mass-a.mass)*vanl + 2*b.mass*vbnl)/M));
        a.vel.sub(van);
        b.vel.add(PVector.mult(Dn,-((a.mass-b.mass)*vbnl + 2*a.mass*vanl)/M));
        b.vel.sub(vbn);
      }
    }
  }
  //update and draw
  //float KE=0, PE=0;//for testing
  for (int i=0; i < balls.size(); i++){
    Ball a = balls.get(i);
    //PE+=a.mass*g*(h-a.pos.y);
    //KE+=0.5*a.mass*a.vel.mag()*a.vel.mag();
    a.update();
    a.draw();
  }
  //println("KE: "+KE+", PE: "+PE+", TE: "+(KE+PE));
}

void mousePressed(){
  //add a large ball on click
  Ball a = new Ball(mouseX, mouseY, 0.0, 0.0);
  a.rad = 20;
  a.mass = 10;
  balls.add(a);
}

class Ball{
  PVector pos, vel;
  int rad;
  float mass;
  color col;
  
  Ball (float x, float y, float velx, float vely){
    pos = new PVector(x,y);
    vel = new PVector(velx, vely);
    rad = 5;
    mass = 1.0;
    col = color(random(100,255), random(100,255), random(100,255));
  }
  
  Ball(){
    pos = new PVector(200,200);
    vel = new PVector(1.0,-1.0);
    rad = 5;
    mass = 1.0;
    col = color(255, 0, 0);
  }
  
  void draw(){
    fill(col);
    ellipse(pos.x, pos.y, rad, rad);
  }
  
  void update(){
    pos.add(vel);
    vel.y += g;
    pos.y += g/2;
    //check for wall collisions
    if (pos.y < rad || pos.y > h-rad){
      float overShoot;
      if (vel.y < 0){
        overShoot = (pos.y-rad)/vel.y;// the overshoot Ratio
      } else {
        overShoot = (pos.y+rad-h)/vel.y;
      }
      pos.y -= overShoot*(vel.y);
      vel.y -= overShoot*g;
      vel.y *= -1;
      vel.y += (1-overShoot)*g;
      pos.y += (1-overShoot)*(vel.y);
    }
    if (pos.x < rad || pos.x > w-rad){
      vel.x *= -1;
    }
  }
}
