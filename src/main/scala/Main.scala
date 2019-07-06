import org.scalajs.dom
import org.scalajs.dom.html.Canvas
import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}

@JSExportTopLevel("Main")
object Main {

  @JSExport
  def start(canvas: Canvas): Unit = {

    val ctx = canvas.getContext("2d")
      .asInstanceOf[dom.CanvasRenderingContext2D]

    ctx.canvas.width = 400
    ctx.canvas.height = 400

    def clear(): Unit = {
      ctx.fillStyle = "black"
      ctx.fillRect(0, 0, 400, 400)
    }

    def draw(): Unit = {
      ctx.fillStyle = "red"
      ctx.fillRect(175, 175, 50, 50)
    }

    dom.window.setInterval(() => {
      clear()
      draw()
    }, 50)
  }

}

