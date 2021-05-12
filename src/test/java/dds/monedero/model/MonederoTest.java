package dds.monedero.model;

import dds.monedero.exceptions.MaximaCantidadDepositosException;
import dds.monedero.exceptions.MaximoExtraccionDiarioException;
import dds.monedero.exceptions.MontoNegativoException;
import dds.monedero.exceptions.SaldoMenorException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class MonederoTest {
  private Cuenta cuenta;

  @BeforeEach
  void init() {
    cuenta = new Cuenta();
  }

  @Test
  void CuentaTiene1500DespuesDeAgregarlos() {
    cuenta.poner(1500);
    assertEquals(1500, cuenta.getSaldo());
  }

  @Test
  void PonerMontoNegativo() {
    assertThrows(MontoNegativoException.class, () -> cuenta.poner(-1500));
  }

  @Test
  void CuentaTieneSumaDeSaldosDespuesDePonerTresDepositos() {
    cuenta.poner(1500);
    cuenta.poner(456);
    cuenta.poner(1900);
    assertEquals(3856, cuenta.getSaldo());
  }

  @Test
  void MasDeTresDepositos() {
    assertThrows(MaximaCantidadDepositosException.class, () -> {
      cuenta.poner(1500);
      cuenta.poner(456);
      cuenta.poner(1900);
      cuenta.poner(245);
    });
  }

  @Test
  void ExtraerMasQueElSaldo() {
    assertThrows(SaldoMenorException.class, () -> {
      cuenta.setSaldo(90);
      cuenta.sacar(1001);
    });
  }

  @Test
  public void ExtraerMasDe1000() {
    assertThrows(MaximoExtraccionDiarioException.class, () -> {
      cuenta.setSaldo(5000);
      cuenta.sacar(1001);
    });
  }

  @Test
  public void ExtraerMontoNegativo() {
    assertThrows(MontoNegativoException.class, () -> cuenta.sacar(-500));
  }

  @Test
  public void DepositoSeAgregaAListaDeMovimientos() {
    cuenta.poner(100);
    //
    assertEquals(1, cuenta.getMovimientos().size());
    assertEquals(100, cuenta.getSaldo());
    assertEquals(100, cuenta.getMovimientos().get(0).getMonto());
    assertTrue(cuenta.getMovimientos().get(0).isDeposito());
    assertEquals(LocalDate.now(), cuenta.getMovimientos().get(0).getFecha());
  }

  @Test
  public void CuentaSePuedeInstanciarConSaldoDeterminado() {
    Cuenta cuentaConMonto = new Cuenta(100);
    assertEquals(100, cuentaConMonto.getSaldo());
  }

  @Test
  public void ExtraerDeUnaCuenta() {
    cuenta.setSaldo(100);
    cuenta.sacar(5);
    //
    assertEquals(95, cuenta.getSaldo());
  }

  @Test
  public void ExtraccionSeAgregaAListaDeMovimientos() {
    cuenta.setSaldo(100);
    cuenta.sacar(5);
    //
    assertEquals(1, cuenta.getMovimientos().size());
    assertEquals(95, cuenta.getSaldo());
    assertEquals(5, cuenta.getMovimientos().get(0).getMonto());
    assertTrue(cuenta.getMovimientos().get(0).isExtraccion());
    assertEquals(LocalDate.now(), cuenta.getMovimientos().get(0).getFecha());
  }

  @Test
  public void MovimientoDepositoFiguraComoDepositadoEnElMismoDia() {
    Movimiento deposito = new Movimiento(LocalDate.now(), 1, true);
    assertTrue(deposito.fueDepositado(LocalDate.now()));
  }

  @Test
  public void MovimientoExtraccionFiguraComoExtraidoEnElMismoDia() {
    Movimiento extraccion = new Movimiento(LocalDate.now(), 1, false);
    assertTrue(extraccion.fueExtraido(LocalDate.now()));
  }

  @Test
  public void SePuedenSetearMovimientosAUnaCuenta(){
    Movimiento deposito = new Movimiento(LocalDate.now(), 1, true);
    Movimiento extraccion = new Movimiento(LocalDate.now(), 1, false);
    cuenta.setMovimientos(Arrays.asList(deposito, extraccion));
    //
    assertEquals(2, cuenta.getMovimientos().size());
  }

}