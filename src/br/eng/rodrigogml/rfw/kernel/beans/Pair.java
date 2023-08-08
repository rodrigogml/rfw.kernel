package br.eng.rodrigogml.rfw.kernel.beans;

import java.io.Serializable;

/**
 * Description: Simples bean para carregar um conjundo de valores tipo chave/valor.<br>
 *
 * @author Rodrigo GML
 * @since 10.0 (24 de out de 2020)
 */
public class Pair<K, V> implements Serializable {

  private static final long serialVersionUID = 5451950348161479230L;

  /**
   * Chave do conjunto
   */
  private K key = null;

  /**
   * Valor do Conjunto
   */
  private V value = null;

  /**
   * Cria um conjunto de par vazio.
   */
  public Pair() {
    super();
  }

  /**
   * Cria um conjunto de par definido.
   */
  public Pair(K key, V value) {
    this.key = key;
    this.value = value;
  }

  /**
   * # chave do conjunto.
   *
   * @return # chave do conjunto
   */
  public K getKey() {
    return key;
  }

  /**
   * # chave do conjunto.
   *
   * @param key # chave do conjunto
   */
  public void setKey(K key) {
    this.key = key;
  }

  /**
   * # valor do Conjunto.
   *
   * @return # valor do Conjunto
   */
  public V getValue() {
    return value;
  }

  /**
   * # valor do Conjunto.
   *
   * @param value # valor do Conjunto
   */
  public void setValue(V value) {
    this.value = value;
  }

}
