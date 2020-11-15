import org.scalajs.dom
import org.scalajs.dom.html.Canvas
import org.scalajs.dom.raw.ImageData

import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}

@JSExportTopLevel("BacteriaSimulation")
object BacteriaSimulation {

  import DifferentialEquations._

  val canvasWidth = 2000
  val canvasHeight = 200

  val delta = 0.01
  val totalTimeSteps = 100000

  val constants = Constants(
    A_max = 1,
    Y_max = 1,
    B_max = 1,
    R = 1,
    M_max = 1,
    C_max = 1,
    P_max = 1,

    alpha_A = 10,
    alpha_Y = 10,
    alpha_Z = 10,
    alpha_B = 1,
    beta = 0.5,
    alpha_M = 0.5,
    alpha_R = 0.1,
    alpha_1 = 100,
    alpha_2 = 100,

    K_A = 0.5,
    K_Y = 0.5,
    K_Z = 0.5,
    K_B = 0.5,
    K_C = 0.5
  )


  @JSExport
  def start(canvas: Canvas): Unit = {

    val ctx = canvas.getContext("2d")
      .asInstanceOf[dom.CanvasRenderingContext2D]

    ctx.canvas.width = canvasWidth
    ctx.canvas.height = canvasHeight

    def clamp(value: Double, max: Double): Double = if(value >= 0) value % max else value + max

    def nutrientLevel(x: Double, y: Double): Double = x / canvasWidth * constants.C_max

    val nutrientBackground: ImageData = {
      val data = ctx.createImageData(canvasWidth, canvasHeight)
      def indexAt(x: Int, y: Int): Int =
        (y * canvasWidth + x) * 4

      for {
        x <- 0 until canvasWidth
        y <- 0 until canvasHeight
        pixelIndex = indexAt(x, y)
      } {
        val level = ((1 - nutrientLevel(x, y)) * 200 + 50).toInt
        data.data.update(pixelIndex+0, level)
        data.data.update(pixelIndex+1, level)
        data.data.update(pixelIndex+2, level)
        data.data.update(pixelIndex+3, 255)
      }
      data
    }

    val bacteriaRadiansPerTime = Math.PI / 5
    val bacteriaMoveSpeed = 1
    val maxTumbleTime = 10

    case class Bacteria(x: Double, y: Double, radians: Double, levels: DataPoint, currentlyTumbling: Boolean, timeUntilTumbleDone: Int) {

      def updated(): Bacteria = {

        val currentRate = rates(EnvironmentalInputs(nutrientLevel(x, y)), constants, levels)
        val newLevels = nextDataPoint(levels, currentRate, delta)

        var newCurrentlyTumbling = currentlyTumbling
        var newTimeUntilTumbleDone = timeUntilTumbleDone

        if(currentlyTumbling) {
          newTimeUntilTumbleDone = timeUntilTumbleDone - 1
          if(timeUntilTumbleDone < 0) {
            newCurrentlyTumbling = false
          }
        }
        else {
          val probabilityOfTumbling = (levels.Y - 0.14) * 10
          if(Math.random() < probabilityOfTumbling) {
            newCurrentlyTumbling = true
            newTimeUntilTumbleDone = (Math.random() * maxTumbleTime).toInt
          }
        }

        val intermediateBacteria = copy(
          levels = newLevels,
          currentlyTumbling = newCurrentlyTumbling,
          timeUntilTumbleDone = newTimeUntilTumbleDone
        )

        if(currentlyTumbling) {
          intermediateBacteria.copy(
            radians = radians + bacteriaRadiansPerTime
          )
        } else {
          intermediateBacteria.copy(
            x = x + Math.cos(radians) * bacteriaMoveSpeed,
            y = clamp(y + Math.sin(radians) * bacteriaMoveSpeed, canvasHeight),
          )
        }
      }

    }

    def clear(): Unit = {
      ctx.fillStyle = "black"
      ctx.fillRect(0, 0, canvasWidth, canvasHeight)
    }

    def drawBacteria(bacteria: Bacteria): Unit = {
      ctx.fillStyle = "red"
      val bacteriaWidth: Double = 5.0
      val bacteriaLength: Double = 10.0

      ctx.translate(bacteria.x, canvasHeight - bacteria.y)
      ctx.rotate(-(bacteria.radians + Math.PI / 2))
      ctx.fillRect(
        x = -bacteriaWidth / 2,
        y = -bacteriaLength / 2,
        w = bacteriaWidth,
        h = bacteriaLength
      )
      ctx.rotate(bacteria.radians + Math.PI / 2)
      ctx.translate(-bacteria.x, bacteria.y - canvasHeight)
    }

    def drawNutrients(): Unit = {
      ctx.putImageData(nutrientBackground, 0, 0)
    }

    var bacteriaModel = Bacteria(
      x = canvasWidth.toDouble / 2,
      y = canvasHeight.toDouble / 2,
      radians = Math.PI / 2,
      levels = DataPoint(
        A = 0,
        Y = 0.151,
        B = 0,
        MMe = 0,
        MMeActive = 0
      ),
      currentlyTumbling = false,
      timeUntilTumbleDone = 0
    )

    //WARM UP
    (0 to 10000).foreach { _ =>
      bacteriaModel = bacteriaModel.updated()
    }

    val n = 1000

    var colony: Array[Bacteria] = new Array[Bacteria](n)
    colony.indices.foreach(colony.update(_, bacteriaModel))

    def draw(): Unit = {
      drawNutrients()
      colony.foreach { bacteria =>
        drawBacteria(bacteria)
      }
    }

    dom.window.setInterval(() => {
      clear()
      colony = colony.map(_.updated())
      draw()
    }, 10)
  }

}