import org.lwjgl.input.{Keyboard, Mouse}
import org.lwjgl.opengl.Display
import org.lwjgl.opengl.GL11._
import java.io._
import java.net.{InetAddress,ServerSocket,Socket,SocketException}

/*

("player_move", "up")
("player_move", "up_release")
("player_move", "down")
("player_move", "down_release")
("player_move", "left")
("player_move", "left_release")
("player_move", "right")
("player_move", "right_release")

("player_aim", x, y)

(PlayerID, "player_pos", x, y)
("bullets", [(x, y), ...])

("join")
("join", PlayerID)

*/

class Player (var x : Float, var y: Float, var rotation: Float) {
  val size = 20
  val speed = 5
  def update(mouseX : Int, mouseY : Int) {
    pointTo(mouseX, mouseY)
    if (Keyboard isKeyDown Keyboard.KEY_A) {
      x -= speed
    }

    if (Keyboard isKeyDown Keyboard.KEY_D) {
      x += speed
    }

    if (Keyboard isKeyDown Keyboard.KEY_W) {
      y += speed
    }

    if (Keyboard isKeyDown Keyboard.KEY_S) {
      y -= speed
    }
  }

  def draw() {
    glPushMatrix()
    glColor3f(0.5f,0.5f,1.0f)       // set the color of the quad (R,G,B,A)
    glTranslatef(x, y, 0)
    glRotatef(rotation, 0, 0, 1)
    // draw quad
    HelloWorld drawQuad(-size / 2, -size / 2, size, size)

    glPopMatrix()
  }

  def pointTo(pointX: Float, pointY: Float) {
    rotation = (Math.atan2(pointY - y, pointX - x) * 180 / Math.PI).toFloat
  }
}

class Bullet (var x : Float, var y : Float, rotation : Float) {
  val speed = 1
  val size = 4
  def update() {
    x += Math.cos(rotation * Math.PI / 180).toFloat
    y += Math.sin(rotation * Math.PI / 180).toFloat
  }

  def draw() {
    glPushMatrix()
    glColor3f(0.5f,0.5f,1.0f)       // set the color of the quad (R,G,B,A)
    glTranslatef(x, y, 0)
    glRotatef(rotation, 0, 0, 1)
    // draw quad
    HelloWorld drawQuad(-size / 2, -size / 2, size, size)
    glPopMatrix()
  }
}

object Net {
  val ia = InetAddress.getByName("localhost")
  val socket = new Socket(ia, 9999)
  val out = new ObjectOutputStream(new DataOutputStream(socket.getOutputStream))
  val in = new ObjectInputStream(new DataInputStream(socket.getInputStream))

  def write(o: Object) {
    out.writeObject(o)
  }

  def read(): Object = {
    in.readObject()
  }

  def join() {
    out.writeObject(("join",))
    out.flush()
  }

  def close() {
    out.close()
    in.close()
    socket.close()
  }
}

object HelloWorld {
  def main(args: Array[String]) {
    println("Hello, world!")
    val player = new Player(100, 100, 0)
    //Display setDisplayMode new DisplayMode(1600,900)
    Display setFullscreen true
    Display create()
    glMatrixMode(GL_PROJECTION)
    glLoadIdentity()
    glOrtho(0, 1600, 0, 900, 1, -1)
    glMatrixMode(GL_MODELVIEW)

    var bullets = List[Bullet]()

    while (!Display.isCloseRequested) {
      // Clear the screen and depth buffer
      glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT)

      val mouseX = Mouse.getX // will return the X coordinate on the Display.
      val mouseY = Mouse.getY // will return the Y coordinate on the Display.
      player update(mouseX, mouseY)

      if (Mouse.isButtonDown(0)) {
        bullets = bullets :+ new Bullet(player.x, player.y, player.rotation)
      }

      bullets foreach (bullet => bullet update())

      player draw()
      bullets foreach (bullet => bullet draw())

      // render OpenGL here
      Display update()
      Thread sleep 12
    }
    Display destroy()
  }

  def drawQuad(x: Float, y: Float, width: Float, height: Float) {
    glBegin(GL_QUADS)
    glVertex2f(x,y)
    glVertex2f(x+width,y)
    glVertex2f(x+width,y+height)
    glVertex2f(x,y+height)
    glEnd()
  }

  def printcb() {
    println("asd")
  }

  def printcbint(): Int = {
    1
  }

  def callcb(cb: (Int) => Unit) {
    println(cb(3))
  }
}


