import scala.collection.mutable.ArrayBuffer


object DifferentialEquations {

  case class EnvironmentalInputs(
                                  C: Double
                                )

  case class DataPoint(
                        A: Double,
                        Y: Double,
                        B: Double,
                        MMe: Double,
                        MMeActive: Double
                      )

  case class Rates(
                    dAdt: Double,
                    dYdt: Double,
                    dBdt: Double,
                    dMMedt: Double,
                    dMMeActivedt: Double
                  )

  case class Constants(
                        A_max: Double,
                        Y_max: Double,
                        B_max: Double,
                        R: Double,
                        M_max: Double,
                        C_max: Double,
                        P_max: Double,

                        alpha_A: Double,
                        alpha_Y: Double,
                        alpha_Z: Double,
                        alpha_B: Double,
                        beta: Double,
                        alpha_M: Double,
                        alpha_R: Double,
                        alpha_1: Double,
                        alpha_2: Double,

                        K_A: Double,
                        K_Y: Double,
                        K_Z: Double,
                        K_B: Double,
                        K_C: Double
                      )

  def rates(i: EnvironmentalInputs, c: Constants, l: DataPoint): Rates = {
    import i._
    import l._
    import c._

    Rates(
      dAdt = alpha_A * MMeActive * (A_max - A) / (K_A + (A_max - A)) - alpha_Y * A * (Y_max - Y) / (K_Y + (Y_max - Y)),
      dYdt = alpha_Y * A * (Y_max - Y) / (K_Y + (Y_max - Y)) - alpha_Z * Y / (K_Z + Y),
      dBdt = alpha_B * MMeActive * (B_max - B) / (K_B + (B_max - B)) - beta * B,
      dMMedt = alpha_1 * C * MMeActive - alpha_2 * MMe + alpha_R * R,
      dMMeActivedt = alpha_2 * MMe - alpha_1 * C * MMeActive - alpha_M * B * MMeActive / (K_C + MMeActive)
    )
  }

  def nextDataPoint(lastDataPoint: DataPoint, rates: Rates, delta: Double): DataPoint =
    DataPoint(
      A = lastDataPoint.A + rates.dAdt * delta,
      Y = lastDataPoint.Y + rates.dYdt * delta,
      B = lastDataPoint.B + rates.dBdt * delta,
      MMe = lastDataPoint.MMe + rates.dMMedt * delta,
      MMeActive = lastDataPoint.MMeActive + rates.dMMeActivedt * delta
    )

}
