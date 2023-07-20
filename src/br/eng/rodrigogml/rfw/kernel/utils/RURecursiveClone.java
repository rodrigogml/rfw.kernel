package br.eng.rodrigogml.rfw.kernel.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import br.eng.rodrigogml.rfw.kernel.exceptions.RFWCriticalException;
import br.eng.rodrigogml.rfw.kernel.exceptions.RFWException;
import br.eng.rodrigogml.rfw.kernel.logger.RFWLogger;
import br.eng.rodrigogml.rfw.kernel.vo.RFWRecursiveClonable;

/**
 * Description: Classe utilit�ria que ajuda a implementa��o da interface {@link RFWRecursiveClonable}.<br>
 *
 * @author Rodrigo Leit�o
 * @since 7.1.0 (18/02/2016)
 */
public class RURecursiveClone {

  @SuppressWarnings({ "unchecked", "rawtypes" })
  public static RFWRecursiveClonable cloneRecursive(RFWRecursiveClonable obj, HashMap<RFWRecursiveClonable, RFWRecursiveClonable> clonedObjects) throws RFWException {
    // Veririca se j� estamos na hash, se estiver retornamos esse objeto
    RFWRecursiveClonable cloned = clonedObjects.get(obj);
    if (cloned != null) {
      return cloned;
    }

    // Se ainda n�o estamos na hash de objetos clonados, nos clonamos e incluimos na hash
    RFWRecursiveClonable clonedvo;
    try {
      try {
        // Primeiro sempre tentamos fazer uma nova inst�ncia ao inv�s de usar o m�todo clone(). D� mesmo conflito com as refer�ncias do Java
        clonedvo = obj.getClass().newInstance();
      } catch (Exception e) {
        clonedvo = (RFWRecursiveClonable) obj.clone();
      }
    } catch (CloneNotSupportedException e1) {
      throw new RFWCriticalException(e1);
    }
    clonedObjects.put(obj, clonedvo);
    // Recupera a lista de m�todos desse objeto
    Method[] methods = obj.getClass().getMethods();
    // Itera essa lista atr�s de m�todos do tipo "get" ou "is"
    for (int i = 0; i < methods.length; i++) {
      String methodname = methods[i].getName();
      if (methodname.startsWith("get") || methodname.startsWith("is")) {
        Method methodget = methods[i];
        Class<?> returntype = methodget.getReturnType();
        Method methodset = null;
        try {
          int x = (methodname.startsWith("is") ? 2 : 3);
          methodset = obj.getClass().getMethod("set" + methodname.substring(x, methodname.length()), returntype);
        } catch (SecurityException e) {
        } catch (NoSuchMethodException e) {
        }
        // Verifica a exist�ncia do M�todo SET
        if (methodset != null) {
          // Verifica se o retorno do m�todo get n�o � nulo
          try {
            Object gettedvalue = methodget.invoke(obj);
            if (gettedvalue != null) {
              // Verifica se o tipo de objeto � um dos que desejamos fazer o "deep clone"
              if (gettedvalue instanceof RFWRecursiveClonable) {
                RFWRecursiveClonable clonedvalue = ((RFWRecursiveClonable) gettedvalue).cloneRecursive(clonedObjects);
                methodset.invoke(clonedvo, clonedvalue);
              } else if (List.class.isAssignableFrom(gettedvalue.getClass())) {
                List clonedvalue = null;
                if (gettedvalue instanceof ArrayList) {
                  clonedvalue = (List) ((ArrayList) gettedvalue).clone();
                } else if (gettedvalue instanceof LinkedList) {
                  clonedvalue = (List) ((LinkedList) gettedvalue).clone();
                } else {
                  throw new RFWCriticalException("O RURecursiveClone n�o suporta a List do tipo '${0}'.", new String[] { gettedvalue.getClass().getCanonicalName() });
                }
                clonedvalue.clear();
                for (Object object : (List) gettedvalue) {
                  if (object instanceof RFWRecursiveClonable) {
                    clonedvalue.add(((RFWRecursiveClonable) object).cloneRecursive(clonedObjects));
                  } else {
                    clonedvalue.add(object);
                  }
                }
                methodset.invoke(clonedvo, clonedvalue);
              } else if (Map.class.isAssignableFrom(gettedvalue.getClass())) {
                Map clonedvalue = null;
                if (gettedvalue instanceof HashMap) {
                  clonedvalue = (Map) ((HashMap) gettedvalue).clone();
                  clonedvalue.clear();

                  // Clonamos n�o s� o valor, mas tamb�m a chave, em alguns casos a chave da Hash pode ser um pr�prio RFWVO como refer�ncia
                  for (Object key : ((HashMap) gettedvalue).keySet()) {
                    Object mapValue = ((HashMap) gettedvalue).get(key); // Recupera antes de clonar a chave ou n�o encontramos nada.

                    if (key instanceof RFWRecursiveClonable) {
                      key = ((RFWRecursiveClonable) key).cloneRecursive(clonedObjects);
                    }

                    if (mapValue instanceof RFWRecursiveClonable) {
                      clonedvalue.put(key, ((RFWRecursiveClonable) mapValue).cloneRecursive(clonedObjects));
                    } else {
                      clonedvalue.put(key, mapValue);
                    }
                  }

                  methodset.invoke(clonedvo, clonedvalue);
                } else {
                  throw new RFWCriticalException("O RURecursiveClone n�o suporta a Map do tipo '${0}'.", new String[] { gettedvalue.getClass().getCanonicalName() });
                }
              } else if (gettedvalue instanceof String || gettedvalue instanceof Long || gettedvalue instanceof Integer || gettedvalue instanceof BigDecimal || gettedvalue instanceof LocalDate || gettedvalue instanceof LocalDateTime || gettedvalue instanceof LocalTime || gettedvalue instanceof Byte || gettedvalue instanceof Character || gettedvalue instanceof Boolean || gettedvalue instanceof Double || gettedvalue instanceof Float || gettedvalue instanceof Date || gettedvalue instanceof Enum<?>) {
                // Objetos imut�veis n�o precisam ser clonados, j� que eles n�o sofrem altera��o basta apontar o novo vo para o mesmo objeto.
                methodset.invoke(clonedvo, gettedvalue);
              } else if (gettedvalue.getClass().isArray() && gettedvalue.getClass().getComponentType().isPrimitive()) {
                // Se �um array de tipos primitivos apenas clonamos o array para que o array n�o seja o mesmo, mas os "objetos" dentro s�o imut�veis, logo n�o precisamos clona-los
                Object clonedvalue = null;
                // Tenho um if para cada tipo porque n�o encontrei um jeito de clona-los ou copia-los sem fazer o cast
                if (gettedvalue instanceof byte[])
                  clonedvalue = ((byte[]) gettedvalue).clone();
                else if (gettedvalue instanceof boolean[])
                  clonedvalue = ((boolean[]) gettedvalue).clone();
                else if (gettedvalue instanceof int[])
                  clonedvalue = ((int[]) gettedvalue).clone();
                else if (gettedvalue instanceof float[])
                  clonedvalue = ((float[]) gettedvalue).clone();
                else if (gettedvalue instanceof long[])
                  clonedvalue = ((long[]) gettedvalue).clone();
                else if (gettedvalue instanceof double[])
                  clonedvalue = ((double[]) gettedvalue).clone();
                else if (gettedvalue instanceof char[])
                  clonedvalue = ((char[]) gettedvalue).clone();
                else if (gettedvalue instanceof short[]) clonedvalue = ((short[]) gettedvalue).clone();

                if (clonedvalue != null) {
                  methodset.invoke(clonedvo, clonedvalue);
                } else {
                  throw new RFWCriticalException("RFW_ERR_200452", new String[] { gettedvalue.getClass().toString(), gettedvalue.getClass().getComponentType().toString() });
                }
              } else if (gettedvalue instanceof Serializable) {
                try {
                  // Como um �ltimo recurso para garantir uma duplica��o do objeto utilizamos o Serializable, serializando o objeto e desserializando, enganamos o java sobre manter a mesma refer�ncia de mem�ria do objeto.
                  // O problema com este m�todo � que se algum objeto dentro do serializable n�o for "serializ�vel" ele vai explodir. Neste caso vamos lan�ar um erro c�ritico, para que o desenvolvedor melhore o objeto ou esta implementa��o do RecursiveClone
                  final ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                  final ObjectOutputStream oo = new ObjectOutputStream(byteStream);
                  oo.writeObject(gettedvalue);
                  oo.flush();
                  oo.close();

                  ObjectInputStream input = new ObjectInputStream(new ByteArrayInputStream(byteStream.toByteArray()));
                  Object clonedValue = input.readObject();
                  input.close();

                  methodset.invoke(clonedvo, clonedValue);

                  // Vou logar como um erro a utiliza��o do Serializable para chamar a aten��o dos objetos que est�o usando essa defini��o. Assim podemos melhorar a implementa��o desse m�todo com o tempo
                  RFWLogger.logError("Utilizado Serializable Clone para o objeto: " + gettedvalue.getClass().toString());
                } catch (Exception e) {
                  throw new RFWCriticalException("RFW_ERR_200450", new String[] { gettedvalue.getClass().toString() }, e);
                }
              }
            }
          } catch (IllegalArgumentException e) {
            RFWLogger.logException(e);
          } catch (IllegalAccessException e) {
            RFWLogger.logException(e);
          } catch (InvocationTargetException e) {
            RFWLogger.logException(e);
          }
        }
      }
    }
    return clonedvo;
  }
}
