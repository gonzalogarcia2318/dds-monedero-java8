package dds.monedero.model;

import dds.monedero.exceptions.MaximaCantidadDepositosException;
import dds.monedero.exceptions.MaximoExtraccionDiarioException;
import dds.monedero.exceptions.MontoNegativoException;
import dds.monedero.exceptions.SaldoMenorException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Cuenta {

  private double saldo = 0;
  private List<Movimiento> movimientos = new ArrayList<>();

  private static final int MAXIMA_CANTIDAD_DEPOSITOS_DIARIOS = 3;
  private static final double MAXIMO_MONTO_EXTRACCION_DIARIO = 1000;

  public Cuenta() {
    saldo = 0;
  }

  public Cuenta(double montoInicial) {
    saldo = montoInicial;
  }

  public void poner(double cuanto) {
    this.validarDeposito(cuanto);
    Deposito deposito = new Deposito(LocalDate.now(), cuanto);
    this.agregarMovimiento(deposito);
    this.setSaldo(calcularValor(deposito));
  }

  public void sacar(double cuanto) {
    this.validarExtraccion(cuanto);
    Extraccion extraccion = new Extraccion(LocalDate.now(), cuanto);
    this.agregarMovimiento(extraccion);
    this.setSaldo(calcularValor(extraccion));
  }

  public void agregarMovimiento(Movimiento movimiento) {
    movimientos.add(movimiento);
  }

  public double getMontoExtraidoA(LocalDate fecha) {
    return getMovimientos().stream()
        .filter(movimiento -> movimiento.fueExtraido(fecha))
        .mapToDouble(Movimiento::getMonto)
        .sum();
  }

  private void validarMontoNegativo(double monto) {
    if (monto <= 0) {
      throw new MontoNegativoException(monto + ": el monto a ingresar debe ser un valor positivo");
    }
  }

  private void validarExtraccion(double cuanto){
    this.validarMontoNegativo(cuanto);

    if (getSaldo() - cuanto < 0) {
      throw new SaldoMenorException("No puede sacar mas de " + getSaldo() + " $");
    }

    double montoExtraidoHoy = getMontoExtraidoA(LocalDate.now());
    double limite = MAXIMO_MONTO_EXTRACCION_DIARIO - montoExtraidoHoy;
    if (cuanto > limite) {
      throw new MaximoExtraccionDiarioException("No puede extraer mas de $ " + MAXIMO_MONTO_EXTRACCION_DIARIO
          + " diarios, límite: " + limite);
    }
  }

  private void validarDeposito(double cuanto){
    this.validarMontoNegativo(cuanto);

    if (this.cantidadDepositos() >= MAXIMA_CANTIDAD_DEPOSITOS_DIARIOS) {
      throw new MaximaCantidadDepositosException("Ya excedio los " + 3 + " depositos diarios");
    }
  }

  private long cantidadDepositos() {
    return getMovimientos().stream().filter(Movimiento::isDeposito).count();
  }

  private double calcularValor(Movimiento movimiento) {
    if (movimiento.isDeposito()) {
      return getSaldo() + movimiento.getMonto();
    } else {
      return getSaldo() - movimiento.getMonto();
    }
  }

  public void setMovimientos(List<Movimiento> movimientos) {
    this.movimientos = movimientos;
  }

  public List<Movimiento> getMovimientos() {
    return movimientos;
  }

  public double getSaldo() {
    return saldo;
  }

  public void setSaldo(double saldo) {
    this.saldo = saldo;
  }

}
