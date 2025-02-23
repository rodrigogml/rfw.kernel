package br.eng.rodrigogml.rfw.kernel.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import br.eng.rodrigogml.rfw.kernel.exceptions.RFWCriticalException;
import br.eng.rodrigogml.rfw.kernel.exceptions.RFWException;
import br.eng.rodrigogml.rfw.kernel.exceptions.RFWRunTimeException;
import br.eng.rodrigogml.rfw.kernel.logger.RFWLogger;
import br.eng.rodrigogml.rfw.kernel.preprocess.PreProcess;
import br.eng.rodrigogml.rfw.kernel.preprocess.PreProcess.PreProcessOption;
import br.eng.rodrigogml.rfw.kernel.rfwmeta.RFWMetaCollectionField;
import br.eng.rodrigogml.rfw.kernel.rfwmeta.RFWMetaEncrypt;
import br.eng.rodrigogml.rfw.kernel.rfwmeta.RFWMetaRelationshipField;
import br.eng.rodrigogml.rfw.kernel.rfwmeta.RFWMetaStringField;
import br.eng.rodrigogml.rfw.kernel.vo.GVO;
import br.eng.rodrigogml.rfw.kernel.vo.RFWMO;
import br.eng.rodrigogml.rfw.kernel.vo.RFWVO;

/**
 * Description: Classe com utilit�rios para tratar objetos apartir de reflex�o.<br>
 *
 * @author Rodrigo Leit�o
 * @since 3.0.0 (SET / 2009)
 */
public class RUReflex {

  /**
   * Construtor privado para classe utilit�ria com m�todos exclusivamente est�ticos.
   */
  private RUReflex() {
  }

  /**
   * Imprime os valores de um objeto simulando uma estrutura XML.<br>
   * Recupera os m�todos Get para imprimir os objetos.
   */
  public static String printObject(Object obj) {
    StringBuilder buff = new StringBuilder();
    if (obj == null) {
      buff.append("<NULL>");
    } else {
      final Class<?> objclass = obj.getClass();
      if (obj instanceof RFWVO) {
        ((RFWVO) obj).printMySelf(buff, new ArrayList<>());
      } else if (obj instanceof RFWMO) {
        buff.append(((RFWMO) obj).printConditions());
      } else {
        buff.append("[Classe do Objeto: " + objclass.getCanonicalName() + "]\n");
        RUReflex.printObject(obj, objclass, buff, new ArrayList<>());
      }
    }
    return buff.toString();
  }

  public static void printObject(Object obj, Class<?> objclass, StringBuilder buff, List<Object> printedobjects) {
    if (obj != null) {

      if (printedobjects.contains(obj)) {
        buff.append("Objeto j� impresso! ").append(objclass.getName()).append(" hashCode: \"").append(obj.hashCode()).append("\"");
      } else {
        if (obj.getClass().isPrimitive()) {
          buff.append(obj);
        } else if (String.class.isInstance(obj)) {
          buff.append(obj);
        } else if (Long.class.isInstance(obj)) {
          buff.append(obj);
        } else if (Integer.class.isInstance(obj)) {
          buff.append(obj);
        } else if (Boolean.class.isInstance(obj)) {
          buff.append(obj);
        } else if (Double.class.isInstance(obj)) {
          buff.append(obj);
        } else if (Float.class.isInstance(obj)) {
          buff.append(obj);
        } else if (Enum.class.isInstance(obj)) {
          buff.append(obj);
        } else if (obj instanceof LocalDate) {
          buff.append(obj.toString());
        } else if (obj instanceof LocalDateTime) {
          buff.append(obj.toString());
        } else if (Date.class.isInstance(obj)) {
          buff.append(obj);
        } else if (BigDecimal.class.isInstance(obj)) {
          buff.append(obj.toString());
        } else if (Class.class.isInstance(obj)) {
          buff.append(((Class<?>) obj).getName());
        } else if (Iterable.class.isInstance(obj)) {
          for (Object item : (Iterable<?>) obj) {
            buff.append("\n<iterableitem>");
            printObject(item, item.getClass(), buff, printedobjects);
            buff.append("</iterableitem>");
          }
        } else if (Map.class.isInstance(obj)) {
          Map<?, ?> map = (Map<?, ?>) obj;
          for (Object key : map.keySet()) {
            buff.append("\n<mapitem>");
            buff.append("\n<key>");
            printObject(key, Object.class, buff, printedobjects);
            buff.append("</key>");
            buff.append("\n<value>");
            printObject(map.get(key), Object.class, buff, printedobjects);
            buff.append("</value>");
            buff.append("</mapitem>");
          }
        } else {
          printedobjects.add(obj);
          buff.append("\n<").append(objclass.getName()).append(" hashCode=\"").append(obj.hashCode()).append("\">");
          // Itera os m�todos do Objeto
          Method[] methods = objclass.getMethods();
          for (Method method : methods) {
            // Verifica se � um m�todo de "get" ou "is" para pegar o valor e imprimir
            if ((method.getName().startsWith("get") || method.getName().startsWith("is")) && !method.getName().equals("getClass")) {
              buff.append("\n<").append(method.getName()).append(">");
              // Recupera o retorno do m�todo
              try {
                Object ret = method.invoke(obj);
                if (ret != null) {
                  if (ret instanceof RFWVO) {
                    ((RFWVO) ret).printMySelf(buff, printedobjects);
                  } else {
                    printObject(ret, method.getReturnType(), buff, printedobjects);
                  }
                }
              } catch (Exception e) {
                buff.append("Exception: '").append(e.getMessage()).append("'");
              }
              buff.append("</").append(method.getName()).append(">");
            }
          }
          buff.append("\n</").append(objclass.getName()).append(">");
        }
      }
    }
  }

  /**
   * Recupera o valor de uma propriedade do bean passado. Caso a propriedade tenha varios nomes separados por pontos ".", estes serao divididos e recuperados recusivamente.
   *
   * @param bean Object objeto o qual o metodo GET ser� chamado
   * @param propertyname String nome da propriedade que deseja-se obter o valor
   * @return Object objeto retornado pelo m�todo get do atributo.
   *
   */
  public static Object getPropertyValue(Object bean, String propertyname) throws RFWException {
    try {
      Object returned = null;
      int index = propertyname.indexOf(".");
      if (index > -1) {
        String firstproperty = propertyname.substring(0, index);
        Object tmpobj = getPropertyValue(bean, firstproperty);
        if (tmpobj != null) returned = getPropertyValue(tmpobj, propertyname.substring(index + 1, propertyname.length()));
      } else {
        // Verifica se temos �ndice/chave, se tivermos mas � a �ltima propriedade tornamos o objeto dentro da lista/hash
        int indexkey = propertyname.indexOf("[");
        String key;
        if (indexkey > -1) {
          key = propertyname.substring(indexkey + 1, propertyname.length() - 1);
          propertyname = propertyname.substring(0, indexkey);
          Object tmpobj = getPropertyValue(bean, propertyname); // Recupera a Hash/Lista deste atributo
          if (tmpobj != null) {
            if (tmpobj instanceof List<?>) {
              List<?> list = (List<?>) tmpobj;
              // Se � uma lista validamos que o index seja num�rico
              if (key == null || !key.matches("[0-9]*")) {
                throw new RFWCriticalException("RFW_ERR_200395", new String[] { propertyname });
              }
              int listindex = Integer.parseInt(key);
              if (listindex >= list.size()) {
                throw new RFWCriticalException("RFW_ERR_200396", new String[] { key, propertyname });
              }
              tmpobj = list.get(listindex);
            } else if (tmpobj instanceof Map<?, ?>) {
              // Extrai o tipo de classe da chave
              int classIndex = key.indexOf('{');
              String clazz = null;
              if (classIndex > -1) {
                clazz = key.substring(classIndex + 1, key.length() - 1);
                key = key.substring(0, classIndex);
              }
              // Com a classe verificamos se � algum tipo conhecido
              Object hashKey = null;
              if ("String".equals(clazz)) {
                hashKey = key;
              } else if ("Long".equals(clazz)) {
                hashKey = Long.parseLong(key);
              }
              if (key == null) {
                throw new RFWCriticalException("RFW_ERR_200397", new String[] { propertyname, key });
              }
              tmpobj = ((Map<?, ?>) tmpobj).get(hashKey);
            }
            returned = tmpobj;
          }
        } else {
          try {
            returned = bean.getClass().getMethod("get" + propertyname.substring(0, 1).toUpperCase() + propertyname.substring(1, propertyname.length()), (Class[]) null).invoke(bean, (Object[]) null);
          } catch (NoSuchMethodException e) {
            try {
              returned = bean.getClass().getMethod("is" + propertyname.substring(0, 1).toUpperCase() + propertyname.substring(1, propertyname.length()), (Class[]) null).invoke(bean, (Object[]) null);
            } catch (NoSuchMethodException e2) {
              returned = bean.getClass().getMethod("are" + propertyname.substring(0, 1).toUpperCase() + propertyname.substring(1, propertyname.length()), (Class[]) null).invoke(bean, (Object[]) null);
            }
          }
        }
      }
      return returned;
    } catch (Exception e) {
      throw new RFWCriticalException("RFW_000032", new String[] { propertyname, bean.getClass().getCanonicalName() }, e);
    }
  }

  /**
   * Define o valor de uma propriedade em um bean. Para recuperar o m�todo de set, procuramos um que receba um objeto da mesma classe que � retornado pelo get.<Br>
   * Caso a propriedade tenha varios nomes separados por pontos ".", estes serao divididos e recuperados recusivamente at� encontrar a propriedade a definir.
   *
   * @param bean Object objeto o qual o metodo SET ser� chamado
   * @param propertyname String nome da propriedade que deseja-se definir o valor
   * @param instantiatenullobjs tenta instanciar objetos que sejam nulos no caminho para definir a propriedade. Para que esta fun��o funcione, os objetos devem ter um construtor sem argumentos.
   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public static void setPropertyValue(Object bean, String propertyname, Object value, boolean instantiatenullobjs) throws RFWException {
    try {
      if (bean == null) {
        throw new NullPointerException("O objeto bean recebido n�o pode ser nulo!");
      }
      int index = propertyname.indexOf(".");
      if (index > -1) {
        String firstproperty = propertyname.substring(0, index);
        Object tmpobj = getPropertyValue(bean, firstproperty);

        // Verifica se a propriedade tem colchetes com a chave/index de cole��es e os separa
        int indexkey = firstproperty.indexOf("[");
        // Verifica se o tmpobj � nulo, e se devemos tentar criar o novo objeto
        if (tmpobj == null) {
          if (!instantiatenullobjs) {
            throw new NullPointerException("Foi encontrado um objeto nulo ao tentar reflex�o para definir o valor de uma propriedade!");
          } else {
            // Se temos �ndices ou chaves nem tentamos criar, n�o temos como criar uma hash ou lista adequadamente
            if (indexkey > -1) throw new RFWCriticalException("RFW_ERR_200398", new String[] { propertyname });
            Class<?> tmpclass = getPropertyTypeByType(bean.getClass(), firstproperty); // Basta procurar pelo type de retorno, pois j� sabemos que o objeto � nulo!
            Constructor<?> constructor = tmpclass.getConstructor();
            if (constructor == null) {
              throw new NullPointerException("N�o foi poss�vel encontrar um construtor sem argumentos para o objeto: " + tmpclass.toString());
            }
            tmpobj = constructor.newInstance();
            setPropertyValue(bean, firstproperty, tmpobj, instantiatenullobjs);
          }
        }
        setPropertyValue(tmpobj, propertyname.substring(index + 1, propertyname.length()), value, instantiatenullobjs);
      } else {
        String key = null;
        // Verifica se a propriedade tem colchetes com a chave/index de cole��es e os separa
        int indexkey = propertyname.indexOf("[");
        if (indexkey > -1) {
          key = propertyname.substring(indexkey + 1, propertyname.length() - 1);
          propertyname = propertyname.substring(0, indexkey);

          Object tmpobj = getPropertyValue(bean, propertyname);
          if (tmpobj instanceof List<?>) {
            List list = (List) tmpobj;
            list.add(value);
          } else if (tmpobj instanceof Map<?, ?>) {
            int classIndex = key.indexOf('{');
            String clazz = null;
            if (classIndex > -1) {
              clazz = key.substring(classIndex + 1, key.length() - 1);
              key = key.substring(0, classIndex);
            }
            Object hashKey = null;
            if ("String".equals(clazz)) {
              hashKey = key;
            }
            if (key == null) {
              throw new RFWCriticalException("RFW_ERR_200397", new String[] { propertyname, key });
            }

            ((Map) tmpobj).put(hashKey, value);
          }
        } else {
          Class<?> propertytype = getPropertyTypeByType(bean.getClass(), propertyname); // Procuramos pelo tipo para definir
          bean.getClass().getMethod("set" + propertyname.substring(0, 1).toUpperCase() + propertyname.substring(1, propertyname.length()), propertytype).invoke(bean, value);
        }
      }
    } catch (Exception e) {
      throw new RFWCriticalException("RFW_ERR_000066", e);
    }
  }

  /**
   * Recupera a classe de uma propriedade do bean passado.<br>
   * Caso a propriedade tenha varios nomes separados por pontos ".", estes serao divididos e recuperados recusivamente. Neste caso, ser� usado o m�todo "get" da propriedade para descobrir sua classe. <b>Este m�todo usar� sempre a classe de retorno do m�todo, ignorando a classe retornada pela inst�ncia passada!</b>
   *
   * @param beanclass Object objeto o qual o metodo GET ser� chamado
   * @param propertyname String nome da propriedade que deseja-se obter o valor
   * @return Class objeto retornado pelo m�todo get do atributo. Null caso algum algum m�todo GET retorne null.
   */
  public static Class<?> getPropertyTypeByType(Class<?> beanclass, String propertyname) throws RFWException {
    Objects.requireNonNull(beanclass, "O objeto beanclass recebido n�o pode ser nulo!");

    Class<?> returned;
    try {
      returned = null;
      int index = propertyname.indexOf(".");
      if (index > -1) {
        String firstproperty = propertyname.substring(0, index);
        Class<?> tmpclass = getPropertyTypeByType(beanclass, firstproperty);
        if (tmpclass != null) returned = getPropertyTypeByType(tmpclass, propertyname.substring(index + 1, propertyname.length()));
      } else {
        // Verifica se temos �ndice/chave, se tivermos mas � a �ltima propriedade tornamos o objeto dentro da lista/hash
        int indexkey = propertyname.indexOf("[");
        if (indexkey > -1) {
          propertyname = propertyname.substring(0, indexkey);
          Class<?> tmpclass = getPropertyTypeByType(beanclass, propertyname); // Recupera a Hash/Lista deste atributo
          if (tmpclass != null) {
            if (List.class.isAssignableFrom(tmpclass)) {
              if (tmpclass.getGenericInterfaces().length > 0) tmpclass = getGenericFromReturnType(beanclass, propertyname)[0];
            } else if (Map.class.isAssignableFrom(tmpclass)) {
              tmpclass = getGenericFromReturnType(beanclass, propertyname)[1];
            }
            returned = tmpclass;
          }
        } else {
          try {
            returned = beanclass.getMethod("get" + propertyname.substring(0, 1).toUpperCase() + propertyname.substring(1, propertyname.length()), (Class[]) null).getReturnType();
          } catch (NoSuchMethodException e) {
            try {
              returned = beanclass.getMethod("is" + propertyname.substring(0, 1).toUpperCase() + propertyname.substring(1, propertyname.length()), (Class[]) null).getReturnType();
            } catch (NoSuchMethodException e2) {
              returned = beanclass.getMethod("are" + propertyname.substring(0, 1).toUpperCase() + propertyname.substring(1, propertyname.length()), (Class[]) null).getReturnType();
            }
          }
        }
      }
    } catch (Throwable e) {
      throw new RFWCriticalException("RFW_ERR_200480", e);
    }
    return returned;
  }

  /**
   * Este m�todo procura as defini��es dos Generics no retorn do m�todo get da propriedade passada.<br>
   * Por exemplo, caso a propriedade da classe passada retorne uma List ou HashMap os Generics definidos ser�o encontrados e suas classes retornadas.
   *
   * @param beanclass Classe do Bean
   * @param propertyName nome da propriedade cujo retorno ser� avaliado.
   * @return Lista das classes dos generics ro retorno do m�toco.
   * @throws RFWException Lan�ado em caso de exce��o
   */
  @SuppressWarnings("rawtypes")
  public static Class<?>[] getGenericFromReturnType(Class<?> beanclass, String propertyName) throws RFWException {
    try {
      Class[] returned = null;
      Type returnType = null;
      try {
        returnType = beanclass.getMethod("get" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1, propertyName.length()), (Class[]) null).getGenericReturnType();
      } catch (NoSuchMethodException e) {
        try {
          returnType = beanclass.getMethod("is" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1, propertyName.length()), (Class[]) null).getGenericReturnType();
        } catch (NoSuchMethodException e2) {
          returnType = beanclass.getMethod("are" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1, propertyName.length()), (Class[]) null).getGenericReturnType();
        }
      }

      if (returnType != null && returnType instanceof ParameterizedType) {
        Type[] acTypeArgs = ((ParameterizedType) returnType).getActualTypeArguments();
        if (acTypeArgs != null) {
          returned = new Class[acTypeArgs.length];
          for (int i = 0; i < acTypeArgs.length; i++) {
            returned[i] = (Class<?>) acTypeArgs[i];
          }
        }
      }
      return returned;
    } catch (Throwable e) {
      throw new RFWCriticalException("RFW_ERR_200480", e);
    }
  }

  /**
   * Recupera a classe de uma propriedade do bean passado.<br>
   * Caso a propriedade tenha varios nomes separados por pontos ".", estes serao divididos e recuperados recusivamente. Neste caso, ser� usado o m�todo "get" da propriedade para descobrir sua classe. <b>Este m�todo usar� sempre o tipo do objeto retornado pela inst�ncia. Logo, se a inst�ncia do objeto retornar null este m�todo ser� incapaz de descobrir a classe da propriedade.</b>
   *
   * @param bean Object objeto o qual o metodo GET ser� chamado
   * @param propertyname String nome da propriedade que deseja-se obter o valor
   * @return Class objeto retornado pelo m�todo get do atributo. Null caso algum algum m�todo GET retorne null.
   */
  public static Class<?> getPropertyTypeByObject(Object bean, String propertyname) throws RFWException {
    if (bean == null) {
      throw new NullPointerException("O objeto bean recebido n�o pode ser nulo!");
    }
    try {
      Class<?> returned = null;
      int index = propertyname.indexOf(".");
      if (index > -1) {
        String firstproperty = propertyname.substring(0, index);
        Object tmpobj;
        try {
          tmpobj = bean.getClass().getMethod("get" + firstproperty.substring(0, 1).toUpperCase() + firstproperty.substring(1, firstproperty.length()), (Class[]) null).invoke(bean, new Object[0]);
        } catch (NoSuchMethodException e) {
          try {
            tmpobj = bean.getClass().getMethod("is" + firstproperty.substring(0, 1).toUpperCase() + firstproperty.substring(1, firstproperty.length()), (Class[]) null).invoke(bean, new Object[0]);
          } catch (NoSuchMethodException e2) {
            tmpobj = bean.getClass().getMethod("are" + firstproperty.substring(0, 1).toUpperCase() + firstproperty.substring(1, firstproperty.length()), (Class[]) null).invoke(bean, new Object[0]);
          }
        }
        if (tmpobj != null) {
          returned = getPropertyTypeByObject(tmpobj, propertyname.substring(index + 1, propertyname.length()));
        }
      } else {
        Object tmpobj;
        try {
          tmpobj = bean.getClass().getMethod("get" + propertyname.substring(0, 1).toUpperCase() + propertyname.substring(1, propertyname.length()), (Class[]) null).invoke(bean, new Object[0]);
        } catch (NoSuchMethodException e) {
          try {
            tmpobj = bean.getClass().getMethod("is" + propertyname.substring(0, 1).toUpperCase() + propertyname.substring(1, propertyname.length()), (Class[]) null).invoke(bean, new Object[0]);
          } catch (NoSuchMethodException e2) {
            tmpobj = bean.getClass().getMethod("are" + propertyname.substring(0, 1).toUpperCase() + propertyname.substring(1, propertyname.length()), (Class[]) null).invoke(bean, new Object[0]);
          }
        }
        if (tmpobj != null) {
          returned = tmpobj.getClass();
        }
      }
      return returned;
    } catch (Throwable e) {
      throw new RFWCriticalException("RFW_ERR_200480", e);
    }
  }

  /**
   * Recupera a classe de uma propriedade do bean passado. Caso a propriedade tenha varios nomes separados por pontos ".", estes serao divididos e recuperados recusivamente.<br>
   * Este m�todo � semelhante ao {@link RUReflex#getPropertyTypeByObject(Object, String)}, a diferen�a � que se uma propriedade retornar nula, ele continua explorando o objeto pela classes de retorno do objeto (como feito pela classe {@link RUReflex#getPropertyTypeByType(Class, String)}.<br>
   *
   *
   * @param bean objeto o qual o metodo GET ser� chamado
   * @param propertyname String nome da propriedade que deseja-se obter o valor
   * @return Class objeto retornado pelo m�todo get do atributo. Null caso algum algum m�todo GET retorne null.
   */
  public static Class<?> getPropertyTypeByObjectAndType(Object bean, String propertyname) throws RFWException {
    if (bean == null) {
      throw new NullPointerException("O objeto bean recebido n�o pode ser nulo!");
    }
    Class<?> returned;
    try {
      returned = null;
      int index = propertyname.indexOf(".");
      if (index > -1) {
        Object tmpobj = getPropertyValue(bean, propertyname.substring(0, index));
        if (tmpobj == null) { // Se o objeto de retorno � nulo, apelamos para usar o tipo de retorno do m�todo
          returned = getPropertyTypeByType(bean.getClass(), propertyname);
        } else { // Caso contr�rio, continuamos usando o objeto
          returned = getPropertyTypeByObjectAndType(tmpobj, propertyname.substring(index + 1, propertyname.length()));
        }
      } else {
        Object tmpobj = getPropertyValue(bean, propertyname);
        if (tmpobj == null) { // Se o objeto de retorno � nulo, apelamos para usar o tipo de retorno do m�todo
          returned = getPropertyTypeByType(bean.getClass(), propertyname);
        } else { // Caso contr�rio, continuamos usando o objeto
          returned = tmpobj.getClass();
        }
      }
    } catch (Throwable e) {
      throw new RFWCriticalException("RFW_ERR_200480", e);
    }
    return returned;
  }

  // TODO 20250221 - Apagar depois que o BIS2 estiver bem validado, a seguir est� o m�todo original do BIS2, acima uma vers�o do BIS10 que reaproveita outros m�todos da classe
  // /**
  // * Recupera a classe de uma propriedade do bean passado. Caso a propriedade tenha varios nomes separados por pontos ".", estes serao divididos e recuperados recusivamente.<br>
  // * Este m�todo � semelhante ao {@link BISUtilsReflex#getPropertyTypeByObject(Class, String)}, a diferen�a � que se uma propriedade retornar nula, ele continua explorando o objeto pela classes de retorno do objeto (como feito pela classe {@link BISUtilsReflex#getPropertyTypeByType(Class, String)}.<br>
  // *
  // *
  // * @param bean objeto o qual o metodo GET ser� chamado
  // * @param propertyname String nome da propriedade que deseja-se obter o valor
  // * @return Class objeto retornado pelo m�todo get do atributo. Null caso algum algum m�todo GET retorne null.
  // */
  // public static Class<?> getPropertyTypeByObjectAndType(Object bean, String propertyname) throws RFWException {
  // if (bean == null) {
  // throw new NullPointerException("O objeto bean recebido n�o pode ser nulo!");
  // }
  // Class<?> returned;
  // try {
  // returned = null;
  // int index = propertyname.indexOf(".");
  // if (index > -1) {
  // String firstproperty = propertyname.substring(0, index);
  // Object tmpobj;
  // try { // Tenta recuperar o objeto de retorno
  // tmpobj = bean.getClass().getMethod("get" + firstproperty.substring(0, 1).toUpperCase() + firstproperty.substring(1, firstproperty.length()), (Class[]) null).invoke(bean, new Object[0]);
  // } catch (NoSuchMethodException e) {
  // try {
  // tmpobj = bean.getClass().getMethod("is" + firstproperty.substring(0, 1).toUpperCase() + firstproperty.substring(1, firstproperty.length()), (Class[]) null).invoke(bean, new Object[0]);
  // } catch (NoSuchMethodException e2) {
  // tmpobj = bean.getClass().getMethod("are" + firstproperty.substring(0, 1).toUpperCase() + firstproperty.substring(1, firstproperty.length()), (Class[]) null).invoke(bean, new Object[0]);
  // }
  // }
  // if (tmpobj == null) { // Se o objeto de retorno � nulo, apelamos para usar o tipo de retorno do m�todo
  // returned = getPropertyTypeByType(bean.getClass(), propertyname);
  // } else { // Caso contr�rio, continuamos usando o objeto
  // returned = getPropertyTypeByObjectAndType(tmpobj, propertyname.substring(index + 1, propertyname.length()));
  // }
  // } else {
  // Object tmpobj;
  // try {
  // tmpobj = bean.getClass().getMethod("get" + propertyname.substring(0, 1).toUpperCase() + propertyname.substring(1, propertyname.length()), (Class[]) null).invoke(bean, new Object[0]);
  // } catch (NoSuchMethodException e) {
  // try {
  // tmpobj = bean.getClass().getMethod("is" + propertyname.substring(0, 1).toUpperCase() + propertyname.substring(1, propertyname.length()), (Class[]) null).invoke(bean, new Object[0]);
  // } catch (NoSuchMethodException e2) {
  // tmpobj = bean.getClass().getMethod("are" + propertyname.substring(0, 1).toUpperCase() + propertyname.substring(1, propertyname.length()), (Class[]) null).invoke(bean, new Object[0]);
  // }
  // }
  // if (tmpobj == null) { // Se o objeto de retorno � nulo, apelamos para usar o tipo de retorno do m�todo
  // returned = getPropertyTypeByType(bean.getClass(), propertyname);
  // } else { // Caso contr�rio, continuamos usando o objeto
  // returned = tmpobj.getClass();
  // }
  // }
  // } catch (Throwable e) {
  // throw new RFWCriticalException("BISERP_000480", e);
  // }
  // return returned;
  // }

  /**
   * Este m�todo vasculha o caminho de propriedades recebido em "propertyPath" e verifica se algum dos atributos retorna uma estrutura de conjunto de objetos (tipicamente uma lista, hashMap, collection, etc.). Caso encontre alguma e que no pr�prio caminho n�o especifique o �ndice ou chave (definidos com os caracteres "[]") o m�todo retornar� o caminho at� o ponto em que o objeto de
   * "cole��o/map/list" foi encontrado.<br>
   * Normalmente, este m�todo retorna o caminho at� onde o m�todo {@link #getPropertyValue(Object, String)} funciona, retornando objetos, j� que o {@link #getPropertyValue(Object, String)} retorna exception quando se depara com algum desses objetos sem a defini��o de �ndice ou que n�o seja o final do caminho.<br>
   * <br>
   * Exemplo:<br>
   * Imagine o caminho "vo1.vo2.vo3.list1.vo5.map1.vo7", onde lista1 sem uma lista de vo4 e map1 � uma hash de vo6. <br>
   * <br>
   * Caso seja passado um objeto vo1, com uma cadeia completa de objetos (nenhum objeto nulo), este m�todo retornar� o caminho "vo1.vo2.vo3.list1", j� que n�o sabemos em qual dos vo4 (dentro da list1) devemos continuar a percorrer o caminho.<br>
   * <bR>
   * No entanto, se passad o caminho "vo1.vo2.vo3.list1[2].vo5.map1.vo7" para a mesma estrutura de objetos, este m�todo retornar� o caminho "vo1.vo2.vo3.list1[2].vo5.map1", j� que agora o m�todo sabe que ao se deparar com a list1 devemos continuar navegando dentro do objeto de �ndice 2. <br>
   * <Br>
   * <b>ATEN��O:</B> Note que este m�todo, assim como o {@link #getPropertyValue(Object, String)} utiliza as inst�ncias dos objetos para navegar o caminho passado, logo se algum m�todo retornar um objeto nulo o m�todo n�o continuar� navegando pelo caminho informado (retornando nulo).
   *
   * @param bean Objeto raiz por onde devemos procurar os objetos.
   * @param propertyPath Caminho a ser percorrido no objeto em busca de alguma estrutura de m�ltiplos objetos.
   * @return Caminho at� onde foi encontramos uma estrutura em que n�o pudemos navegar mais, ou NULL caso em algum ponto do caminho nos deparemos com um objeto nulo impedindo de continuar a percorrer o caminho, ou se algum objeto retornou nulo no meio do caminho, nos impedindo de encontrar uma estrutura.
   * @throws RFWException Lan�ado caso ocorra algum problema durante a execu��o do m�todo.
   */
  public static String findIterableInPath(Object bean, String propertyPath) throws RFWException {
    // Quebramos o caminho de acordo com os "."
    final String[] properties = propertyPath.split("\\."); // Cada properties j� inclui seus pr�prios "[...]"
    // Iteramos o caminho para tentar obter o valor parte a parte em busca de um objeto iter�vel
    Object lastBean = bean;
    String property = "";
    for (int i = 0; i < properties.length; i++) {
      property += (property.length() > 0 ? '.' : "") + properties[i];
      lastBean = getPropertyValue(lastBean, properties[i]);
      if (lastBean == null) return null;
      if (lastBean instanceof List<?>) {
        return property;
      } else if (lastBean instanceof Map<?, ?>) {
        return property;
      }
    }
    return null;
  }

  /**
   * Limpa o ID do objeto e recursivamente de todos os objetos de relacionamento do tipo composi��o.<br>
   * Este m�todo tem a finalidade de duplicar/clonar um objeto, permitindo que ao ser persistido ele possa ser identificado como um objeto novo.<br>
   * Lembre-se que atributos que tenham a restri��o de 'unique=true', continuar�o impedindo que o novo objeto seja persistido. Nesses dados este m�todo n�o faz nenhum tipo de altera��o.
   *
   * @param vo RFWVO a ter seus IDs limpos
   * @throws RFWException
   */
  @SuppressWarnings("unchecked")
  public static <VO extends RFWVO> void cleanOwnIDs(VO vo) throws RFWException {
    vo.setId(null);
    for (Field field : vo.getClass().getDeclaredFields()) {
      final Annotation ann = getRFWMetaAnnotation(field);
      if (ann instanceof RFWMetaRelationshipField) {
        switch (((RFWMetaRelationshipField) ann).relationship()) {
          case INNER_ASSOCIATION:
          case MANY_TO_MANY:
          case PARENT_ASSOCIATION:
          case ASSOCIATION:
          case WEAK_ASSOCIATION:
            // Para esses casos n�o remove nem faz nada, pois n�o ser�o persistidos com o novo objeto, e sim s� relacionados
            break;
          case COMPOSITION_TREE:
          case COMPOSITION: {
            final Object value = getPropertyValue(vo, field.getName());
            if (value != null) {
              if (value instanceof RFWVO) {
                cleanOwnIDs((VO) value);
              } else if (value instanceof Map) {
                for (Object mapValue : ((Map<?, ?>) value).values()) {
                  cleanOwnIDs((VO) mapValue);
                }
              } else if (value instanceof List) {
                for (Object listValue : (List<?>) value) {
                  cleanOwnIDs((VO) listValue);
                }
              } else {
                throw new RFWCriticalException("Imposs�vel duplicar o VO! Tipo de atributo desconhecido no atributo '${0}' da classe '${1}'.", new String[] { field.getName(), vo.getClass().getCanonicalName() });
              }
            }
          }
            break;
        }
      }

    }
  }

  /**
   * Este m�todo recebe um objeto e um nome de propriedade. Tentar� encontrar uma RFWMeta Annotation neste atributo.
   *
   * @param voClass Classe a ser analizada
   * @param attribute Nome do atributo para encontrar a RFWMeta Annotation
   * @return uma annotation RFWMeta ou NULL caso nenhuma seja encontrada.
   * @throws RFWException
   */
  @SuppressWarnings("unchecked")
  public static Annotation getRFWMetaAnnotation(Class<? extends RFWVO> voClass, String attribute) throws RFWException {
    Annotation ann = null;
    Field field = null;

    // Se houver neasted properties, recuperamos a classe do pen�ltimo atributo, para que dele possamos recuperar o Field
    if (attribute.indexOf(".") > -1) {
      final int lindex = attribute.lastIndexOf(".");
      final Class<?> tmpClass = getPropertyTransparentType(voClass, attribute.substring(0, lindex));
      if (tmpClass == null || !RFWVO.class.isAssignableFrom(tmpClass)) throw new RFWCriticalException("Imposs�vel obter o caminho '${0}' na clase '${1}' se o tipo retornado n�o � um RFWVO!", new String[] { attribute, voClass.getCanonicalName() });
      voClass = (Class<? extends RFWVO>) tmpClass;
      attribute = attribute.substring(lindex + 1, attribute.length());
    }

    // Continuamos normalmente agora que pegamos a classe correta para obter o atributo
    try {
      if (!"id".equals(attribute)) field = getDeclaredFieldRecursively(voClass, getCleanPath(attribute));
    } catch (Exception e) {
      throw new RFWCriticalException("Falha ao encontrar uma RFWMeta annotation no atributo '${0}' da classe '${1}'", new String[] { attribute, voClass.getCanonicalName() }, e);
    }
    if (field != null) {
      final Annotation[] anns = field.getDeclaredAnnotations();
      for (Annotation annotation : anns) {
        final String basepackage = RFWMetaStringField.class.getPackage().getName();
        if (annotation.annotationType().getCanonicalName().startsWith(basepackage + ".RFWMeta")) {
          ann = annotation;
          break;
        }
      }
    }
    return ann;
  }

  /**
   * Busca um 'field' por reflax�o na classe passada, se n�o encontrar continua buscando recursivamente nas classes pais at� encontrar.
   *
   * @param voClass Classe extendendo o {@link RFWVO} para obter o 'field'
   * @param attribute Nome do atributo ('field') para buscar.
   * @return Objeto representando a defini��o do 'field' no objeto passado ou classe pai mais imediata que contenha a defini��o.
   * @throws RFWException Lan�ado caso o field n�o seja encontrado na hierarquia ('RFW_000020')
   */
  @SuppressWarnings("unchecked")
  public static Field getDeclaredFieldRecursively(Class<? extends RFWVO> voClass, String attribute) throws RFWException {
    PreProcess.requiredNonNull(voClass);
    PreProcess.requiredNonNull(attribute);

    Field field = null;
    while (voClass != null && field == null) {
      try {
        field = voClass.getDeclaredField(attribute);
      } catch (NoSuchFieldException e) {
        voClass = (Class<? extends RFWVO>) voClass.getSuperclass();
      }
    }
    if (field == null) throw new RFWCriticalException("RFW_000020", new String[] { attribute, voClass.getCanonicalName() });
    return field;
  }

  /**
   * Busca as defini��es dos fields da classe com o m�todo {@link Class#getDeclaredFields()} da classe passada e de todas as classes acima na hierarquia.
   *
   * @param voClass Classe do objeto que extenda {@link RFWVO} para objter as declara��es de todos os fields na hierarquia.
   * @return Array com a declara��o de todos os fields encontrados.
   * @throws RFWException Lan�ado em caso de falha do sistema.
   */
  @SuppressWarnings("unchecked")
  public static Field[] getDeclaredFieldsRecursively(Class<? extends RFWVO> voClass) throws RFWException {
    PreProcess.requiredNonNull(voClass);

    Field[] fields = new Field[0];
    while (voClass != null) {
      fields = RUArray.addValues(fields, voClass.getDeclaredFields());
      voClass = (Class<? extends RFWVO>) voClass.getSuperclass();
    }
    return fields;
  }

  /**
   * Este m�todo extrai o caminho "limpo" a partir de um caminho completo.<br>
   * O caminho limpo � o caminho dos atributos mas sem os colchetes de �ndices ou chaves de hash. Facilitando assim acompara��o do atributo sendo tratado.<br>
   * Ex: "itemcodelist[0].itemvo" retorna "itemcodelist.itemvo". Ex: "itemcodehash{key}.itemvo" retorna "itemcodehash.itemvo".
   *
   * @param fullpath
   * @return caminho limpo sem as defini��es de cole��es.
   */
  public static String getCleanPath(String fullpath) {
    return fullpath.replaceAll("\\[[^\\]]*\\]", "").replaceAll("\\{[^\\}]*\\}", "");
  }

  /**
   * Este m�todo retorna o tipo (classe) de um atributo, navegando por Neasted Properties no padr�o do RFWDeprec, semelhante ao {@link #getPropertyTypeByType(Class, String)} mas ignorando as Listas/Hashs. Isto �, sempre que encontramos uma {@link List} ou {@link Map} no caminho o RFWDeprec retornar� o tipo de objeto que � armazenado dentro dessas cole��es. Para outros tipos, o tipo do objeto �
   * retornado normalmente.<br>
   * <br>
   * A resolu��o de qual � o objeto dentro da collection � feito da seguinte maneira:
   * <li>Verificamos se o atributo tem uma {@link RFWMetaRelationshipField} e o atributo {@link RFWMetaRelationshipField#targetRelationship()} definido. Caso verdadeiro, essa defini��o � utilizada.
   * <li>Verificamos os Generics definidos. Caso tenha essa classe � utilizada para continuar a busca.
   *
   * @param beanClass Classe inicial para percorrer o caminho at� o atributo final.
   * @param propertyName Nome da Propriedade, com ou sem Neasted Properties.
   * @throws RFWException Lan�ado em Caso de Exception
   */
  @SuppressWarnings("unchecked")
  public static <VO extends RFWVO> Class<?> getPropertyTransparentType(Class<VO> beanClass, String propertyName) throws RFWException {
    Objects.requireNonNull(beanClass, "O atributo beanclass n�o pode ser nulo!");
    Objects.requireNonNull(propertyName, "O atributo propertyName n�o pode ser nulo!");

    // Limpamos o caminho pois s� vamos trabalhar com a Classe dos objetos
    propertyName = getCleanPath(propertyName);

    int index = propertyName.indexOf(".");
    if (index > -1) {
      // Se temos neasted properties, chamamos recursivamente at� chegar na �ltima propriedade
      Class<VO> valueVOClass = (Class<VO>) getPropertyTransparentType(beanClass, propertyName.substring(0, index));
      // Se ainda temos Neasted Properties, mas o tipo retornado n�o � um RFWVO, Exception!!!
      if (valueVOClass == null || !RFWVO.class.isAssignableFrom(valueVOClass)) throw new RFWCriticalException("Imposs�vel obter o caminho '${0}' na clase '${1}' se o tipo retornado n�o � um RFWVO!", new String[] { propertyName, beanClass.getCanonicalName() });
      return getPropertyTransparentType(valueVOClass, propertyName.substring(index + 1, propertyName.length()));
    } else {
      try {
        // Se n�o � uma Neasted Property, recuperamos a classe do atributo e verificamos as listas/hashs
        Field field = beanClass.getDeclaredField(propertyName);
        Class<?> type = field.getType();
        if (List.class.isAssignableFrom(type)) {
          // Procuramos a Annotation para pegar o atributo Target
          RFWMetaRelationshipField ann = field.getAnnotation(RFWMetaRelationshipField.class);
          if (ann != null && ann.targetRelationship() != RFWVO.class) {
            return ann.targetRelationship();
          } else {
            // Se N�o temos a annotation ou o target, tentamos recuperar o generics da lista
            final Class<?>[] gi = getGenericsFromField(field);
            if (gi != null && gi.length > 0) return gi[0];
          }
        } else if (Map.class.isAssignableFrom(type)) {
          // Procuramos a Annotation para pegar o atributo Target
          RFWMetaRelationshipField ann = field.getAnnotation(RFWMetaRelationshipField.class);
          if (ann != null && ann.targetRelationship() != RFWVO.class) {
            return ann.targetRelationship();
          } else {
            // Se N�o temos a annotation ou o target, tentamos recuperar o generics da lista
            final Class<?>[] gi = getGenericsFromField(field);
            if (gi != null && gi.length > 1) return gi[1]; // Retorna o �ndice do generics do Value e n�o da chave
          }
        } else {
          // Qualquer outro tipo � s� retornar
          return type;
        }
      } catch (Throwable e) {
        throw new RFWCriticalException("Falha ao obter o atributo '${0}' da classe '${1}'.", new String[] { propertyName, beanClass.getCanonicalName() }, e);
      }
    }
    return null;
  }

  /**
   * Recupera as classes definidas no Generics da Declara��o de um attributo (Field) de uma classe.
   *
   * @param field Atributo Field j� recuperado por reflex�o.
   * @return Conjunto de Classes que forem definidos no Generics da Declara��o de um Attributo.
   * @throws RFWException
   */
  public static Class<?>[] getGenericsFromField(Field field) throws RFWException {
    Objects.requireNonNull(field, "Classe � obrigat�rioa!");

    Class<?>[] ret = null;
    final Type type = field.getGenericType();
    if (type != null && type instanceof ParameterizedType) {
      Type[] acTypeArgs = ((ParameterizedType) type).getActualTypeArguments();
      if (acTypeArgs != null) {
        ret = new Class[acTypeArgs.length];
        for (int i = 0; i < acTypeArgs.length; i++) {
          ret[i] = (Class<?>) acTypeArgs[i];
        }
      }
    }
    return ret;
  }

  /**
   * Procura um atributo em um RFWVO, procura se h� alguma RFWMetaAnnotation no atributo, e se esta RFWMeta tiver o atributo "preProcess" retorna seu valor. Caso contr�rio retornar� null.<br>
   * Este m�todo n�o lan�a exce��es caso o n�o tenha a RFWMeta, ou se a mesma n�o tiver o atributo caption.
   *
   * @param voClass Classe a ser analizada
   * @param attribute Atributo para procurar pela RFWMeta
   * @return O valor da Caption, ou NULL caso n�o tenha sido encontrado
   * @throws RFWException
   */
  public static PreProcessOption[] getRFWMetaAnnotationPreProcess(Class<? extends RFWVO> voClass, String attribute) throws RFWException {
    PreProcessOption[] preProcess = null;
    Annotation ann = getRFWMetaAnnotation(voClass, attribute);
    if (ann != null) {
      try {
        Method m = ann.getClass().getMethod("preProcess");
        preProcess = (PreProcessOption[]) m.invoke(ann);
      } catch (Exception e) {
        try {
          Method m = ann.getClass().getMethod("preProcess");
          preProcess = new PreProcessOption[] { (PreProcessOption) m.invoke(ann) };
        } catch (Exception e2) {
          // Qualquer falha ao tentar obter o preProcess n�o � reportado nem tratado, apenas retornamos null
        }
      }
    }
    return preProcess;
  }

  /**
   * Este m�todo recebe um campo field diretamente e tentar� encontrar uma RFWMeta Annotation neste atributo.
   *
   * @param field Objeto field da reflex�o do Java para encontrar a RFWMeta Annotation
   * @return uma annotation RFWMeta ou NULL caso nenhuma seja encontrada.
   * @throws RFWException
   */
  public static Annotation getRFWMetaAnnotation(Field field) throws RFWException {
    Annotation ann = null;
    final Annotation[] anns = field.getDeclaredAnnotations();
    for (Annotation annotation : anns) {
      final String basepackage = RFWMetaStringField.class.getPackage().getName();
      if (annotation.annotationType().getCanonicalName().startsWith(basepackage + ".RFWMeta")) {
        ann = annotation;
        break;
      }
    }
    return ann;
  }

  /**
   * Procura um atributo em um RFWVO, procura se h� alguma RFWMetaAnnotation no atributo, e se esta RFWMeta tiver o atributo "caption" retorna seu valor. Caso contr�rio retornar� null.<br>
   * Este m�todo n�o lan�a exce��es caso o n�o tenha a RFWMeta, ou se a mesma n�o tiver o atributo caption.
   *
   * @param voClass Classe a ser analizada
   * @param attribute Atributo para procurar pela RFWMeta
   * @return O valor da Caption, ou NULL caso n�o tenha sido encontrado
   * @throws RFWException
   */
  public static String getRFWMetaAnnotationCaption(Class<? extends RFWVO> voClass, String attribute) throws RFWException {
    String caption = null;
    Annotation ann = getRFWMetaAnnotation(voClass, attribute);
    if (ann != null) {
      try {
        Method m = ann.getClass().getMethod("caption");
        caption = (String) m.invoke(ann);
      } catch (Exception e) {
        // Qualquer falha ao tentar obter o caption n�o � reportado nem tratado, apenas retornamos null
      }
    }
    return caption;
  }

  /**
   * Procura um atributo em um RFWVO, procura se h� alguma RFWMetaAnnotation no atributo, e se esta RFWMeta tiver o atributo "required" retorna seu valor. Caso contr�rio retornar� null.<br>
   * Este m�todo n�o lan�a exce��es caso o n�o tenha a RFWMeta, ou se a mesma n�o tiver o atributo caption.
   *
   * @param voClass Classe a ser analizada
   * @param attribute Atributo para procurar pela RFWMeta
   * @return O valor da Caption, ou NULL caso n�o tenha sido encontrado
   * @throws RFWException
   */
  public static Boolean getRFWMetaAnnotationRequired(Class<? extends RFWVO> voClass, String attribute) throws RFWException {
    Boolean required = null;
    Annotation ann = getRFWMetaAnnotation(voClass, attribute);
    if (ann != null) {
      try {
        Method m = ann.getClass().getMethod("required");
        required = (Boolean) m.invoke(ann);
      } catch (Exception e) {
        // Qualquer falha ao tentar obter o caption n�o � reportado nem tratado, apenas retornamos null
      }
    }
    return required;
  }

  /**
   * Este m�todo tenta montar um caminho com os 'captions' das RFWMetaAnnotations recursivamente, no padr�o "Caption 1 > Caption 2 > Caption 3".<br>
   * Caso algum atirbuto n�o tenha a RFWMetaAnnotation o m�todo lan�ar� exception.<br>
   * Caso o caption esteja vazio (""), o passo ser� ignorado para evitar a concatena��o "> >". <br>
   * Este m�todo n�o considera os indeces de Lista ou chaves de Hash para colocar no caminho. Essa op��o deve ser criada em outro m�todo.
   *
   * @param voClass Classe do VO do come�o do caminho.
   * @param attributePath Caminho partindo do voClass at� o atributo desejado
   * @return Caption montado recursivamente.
   * @throws RFWException
   */
  public static String getRFWMetaAnnotationFullCaption(Class<? extends RFWVO> voClass, String attributePath) throws RFWException {
    boolean showListIndex = true;
    int startindex = 0;
    StringBuilder buff = new StringBuilder(20);
    while (startindex < attributePath.length()) {
      int finalindex = attributePath.indexOf(".", startindex);
      if (finalindex == -1) finalindex = attributePath.length();
      if (buff.length() > 0) buff.append(" > ");
      final String path = attributePath.substring(0, finalindex);
      buff.append(getRFWMetaAnnotationCaption(voClass, path));
      if (showListIndex && path.matches(".*\\[[0-9]+\\]")) buff.append("[").append(Integer.parseInt(path.substring(path.indexOf("[") + 1, path.indexOf("]"))) + 1).append("]");
      startindex = finalindex + 1;
    }
    return buff.toString();
  }

  /**
   * Procura um RFWVO em uma lista caso o atributo passado seja 'equals' o valor. <br>
   * Note que se houver mais do que um RFWVO com o mesmo ID, ser� retornado o primeiro encontrado.
   *
   * @param <VO> Tipo do Objeto que extenda {@link RFWVO}
   * @param list Lista de RFWVO
   * @param path Caminho dos atirbutos aninhados at� a propriedade que precisa ser validada.
   * @param value Valor para ser passado no equals e comparado. Caso seja passado 'null', o m�todo verificar� se o retorno da propriedade passada em 'path' tamb�m � nulo. OBS: Note que se qualquer par�metro dos atirbutos aninhados retornar nulo, o resultado ser� 'true' caso value seja 'null'.
   * @return Primeiro objeto cujo atributo passado seja 'equals' o 'value' passado.
   * @throws RFWException
   */
  public static <VO extends RFWVO> VO getRFWVO(List<VO> list, String path, Object value) throws RFWException {
    RFWException[] ex = new RFWException[1];
    List<VO> filteredList = list.stream().filter(vo -> {
      try {
        Object v = getPropertyValue(vo, path);
        if (v == null && value == null) return true;
        if (v != null && v.equals(value)) return true;
      } catch (RFWException e) {
        ex[1] = e;
      }
      return false;
    }).collect(Collectors.toList());
    if (ex[0] != null) throw ex[0]; // se tiver uma exception dentro do bloco Lambda, lan�a ela fora
    if (filteredList.size() > 0) return filteredList.get(0);
    return null;
  }

  /**
   * Este m�todo recebe um objeto e um nome de propriedade. Tentar� encontrar uma RFWDAOEncrypt neste atributo.
   *
   * @param voClass Classe a ser analizada
   * @param attribute Nome do atributo para encontrar a RFWMeta Annotation
   * @return uma annotation RFWMeta ou NULL caso nenhuma seja encontrada.
   * @throws RFWException
   */
  @SuppressWarnings("unchecked")
  public static RFWMetaEncrypt getRFWDAOEncryptAnnotation(Class<? extends RFWVO> voClass, String attribute) throws RFWException {
    RFWMetaEncrypt ann = null;
    Field field = null;

    // Se houver neasted properties, recuperamos a classe do pen�ltimo atributo, para que dele possamos recuperar o Field
    if (attribute.indexOf(".") > -1) {
      final int lindex = attribute.lastIndexOf(".");
      final Class<?> tmpClass = getPropertyTransparentType(voClass, attribute.substring(0, lindex));
      if (tmpClass == null || !RFWVO.class.isAssignableFrom(tmpClass)) throw new RFWCriticalException("Imposs�vel obter o caminho '${0}' na clase '${1}' se o tipo retornado n�o � um RFWVO!", new String[] { attribute, voClass.getCanonicalName() });
      voClass = (Class<? extends RFWVO>) tmpClass;
      attribute = attribute.substring(lindex + 1, attribute.length());
    }

    // Continuamos normalmente agora que pegamos a classe correta para obter o atributo
    try {
      if (!"id".equals(attribute)) field = voClass.getDeclaredField(RUReflex.getCleanPath(attribute));
    } catch (Exception e) {
      throw new RFWCriticalException("Falha ao encontrar uma RFWMeta annotation no atributo '${0}' da classe '${1}'", new String[] { attribute, voClass.getCanonicalName() }, e);
    }
    if (field != null) {
      ann = field.getAnnotation(RFWMetaEncrypt.class);
    }
    return ann;
  }

  /**
   * Este m�todo obtem uma classe de RFWVO e vasculha todos os objetos que s�o dependentes/relacionados a este objeto. Em outras palavras retorna os atributos que precisam ser recuperados para uma atualiza��o.<br>
   * A busca consiste em recuperar todos os objetos que estejam associados (tipos ASSOCIATION, PARENT_ASSOCIATION ou MANY_TO_MANY), para que seja poss�vel a detec��o da desassocia��o caso o objeto n�o esteja mais presente. E recuperamos recursivamente os objetos de composi��o (tipo COMPOSITION) para o mesmo fim.
   *
   * @param clazz Classe do VO que vamos vasculhar
   * @param path Caminho usado at� chegar na classe sendo passada. Usado durante a recurs�o. Para obter os atributos do objeto raiz passe o valor "".
   * @return Lista com o caminho at� a propriedade id dos objetos que precisam ser recupeados. Ex: 'users.companies.id'.
   * @throws RFWException
   */
  @SuppressWarnings("unchecked")
  private static <VO extends RFWVO> List<String> getRFWVOUpdateAttributes(Class<VO> clazz, String path) throws RFWException {
    final LinkedList<String> list = new LinkedList<>();

    // iteramos todos os fields da classe em busca da annotation RFWMetaRelationshipField
    for (Field field : clazz.getDeclaredFields()) {
      final RFWMetaRelationshipField ann = field.getAnnotation(RFWMetaRelationshipField.class);
      if (ann != null) {
        switch (ann.relationship()) {
          case COMPOSITION:
            list.add(addPath(path, field.getName()) + ".id");

            Class<? extends RFWVO> entityType = null;
            if (!ann.targetRelationship().equals(RFWVO.class)) {
              entityType = ann.targetRelationship();
            } else if (RFWVO.class.isAssignableFrom(field.getType())) {
              entityType = (Class<? extends RFWVO>) field.getType();
            }
            if (entityType == null) {
              throw new RFWCriticalException("N�o foi poss�vel encontrar o RFWVO no relacionamento da entidade '${0}' no atributo '${1}\". Relacionamentos com cole��es � obrigat�rio a defini��o do atributo 'targetRelationship' na RFWMetaAnnotation.", new String[] { clazz.getCanonicalName(), field.getName() });
            }
            if (!clazz.equals(entityType)) {
              // Quando a classe atual e a da compois��o s�o as mesmas encontramos um loop, o que acontece quando utilizado em casos hier�rquicos.
              // Neste caso paramos de buscar os atributos pq termina em loop eterno.
              list.addAll(getRFWVOUpdateAttributes(entityType, addPath(path, field.getName())));
            }
            break;
          case WEAK_ASSOCIATION:
          case ASSOCIATION:
          case PARENT_ASSOCIATION:
          case INNER_ASSOCIATION:
          case MANY_TO_MANY:
            list.add(addPath(path, field.getName()) + ".id");
            break;
          case COMPOSITION_TREE:
            // Salvamos os caminhos encontratos na cache de CompositionTree para vasculhar melhor, mas n�o continuamos navegando mais.
            list.add(addPath(path, field.getName()) + ".id");
            break;
        }
      }
      final RFWMetaCollectionField colAnn = field.getAnnotation(RFWMetaCollectionField.class);
      if (colAnn != null) {
        list.add(addPath(path, field.getName()) + "@");
      }
    }
    return list;
  }

  /**
   * M�todo auxiliar para adicionar um caminho na estrutura existente.<br>
   *
   * @param path Caminho atual.
   * @param newAdd Peda�o a ser adicionado ao caminho atual.
   * @return Retorna o caminho pai. Ex:
   *         <li>Se passado "a.b.c.d.e" este m�todo deve retornar "a.b.c.d".
   *         <li>Se passado "a" o m�todo deve retornar "" j� que vazio � caminho raiz.
   *         <li>Se passado "" retornamos null j� que n�o temos um caminho pai.
   */
  public static String addPath(String path, String newAdd) {
    if (path == null || "".equals(path)) {
      return newAdd;
    } else {
      return path + "." + newAdd;
    }
  }

  /**
   * Este m�todo obtem uma classe de RFWVO e vasculha todos os objetos que s�o dependentes/relacionados a este objeto. Em outras palavras retorna os atributos que precisam ser recuperados para uma atualiza��o.<br>
   * A busca consiste em recuperar todos os objetos que estejam associados (tipos ASSOCIATION, PARENT_ASSOCIATION ou MANY_TO_MANY), para que seja poss�vel a detec��o da desassocia��o caso o objeto n�o esteja mais presente. E recuperamos recursivamente os objetos de composi��o (tipo COMPOSITION) para o mesmo fim.
   *
   * @param clazz Classe do VO que vamos vasculhar
   * @return Lista com o caminho at� a propriedade id dos objetos que precisam ser recupeados. Ex: 'users.companies.id'.
   * @throws RFWException
   */
  public static <VO extends RFWVO> String[] getRFWVOUpdateAttributes(Class<VO> clazz) throws RFWException {
    return getRFWVOUpdateAttributes(clazz, "").toArray(new String[0]);
  }

  /**
   * Este m�todo recupera a pilha dos m�todos que foram sendo chamados at� a chamada deste m�todo. Cria o caminho dos m�todos chamados como uma pilha de StackTrace
   *
   * @param offset permite "ocultar" as �ltimas chamadas do m�todo. Por exemplo, se n�o se deseja registrar o pr�prio m�todo getInvoker deve-se passar 1. Se n�o deseja registrar o m�todo que chamou este M�todo, deve se passar 2, e assim por diante. Valor m�nimo 0, vaor muito alto pode n�o sobrar nada na pilha para imprimir.
   * @param stacksize tamanho m�ximo da pilha para retornar. Pode limitar a quantidade m�xima de linhas a serem retornadas.
   */
  public static final String getInvoker(int offset, int stacksize) {
    final StringBuilder buff = new StringBuilder();

    if (offset < 0) offset = 0;

    StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
    for (int i = offset; i < stacktrace.length && i - offset < stacksize; i++) {
      buff.append('\t').append(stacktrace[i].getClassName()).append(".").append(stacktrace[i].getMethodName()).append("(").append(stacktrace[i].getFileName()).append(":").append(stacktrace[i].getLineNumber()).append(")").append("\r\n");
    }
    return buff.toString();
  }

  /**
   * Cria o caminho para um item dentro de uma hash.
   *
   * @param attribute Nome do atributo que cont�m a hash.
   * @param key Representa��o em String do objeto chave da hash.
   * @param clazz Classe da chave de Hash. Atualmente suporta apenas String e Long.
   * @param basepath Caminho base at� chegar neste atributo.
   * @return Caminho que identificau atributo em um objeto dentro de uma hash.
   */
  public static String getAttributePath(String attribute, Object key, Class<?> clazz, String basepath) {
    if (String.class.isAssignableFrom(clazz)) {
      key = key + "{String}";
    } else if (Long.class.isAssignableFrom(clazz)) {
      key = key + "{Long}";
    } else {
      throw new RFWRunTimeException("Classe n�o suportada como chave de hash!");
    }
    attribute = attribute + "[" + key + "]";
    if (basepath != null) {
      return basepath + "." + attribute;
    } else {
      return attribute;
    }
  }

  /**
   * Monta um caminho baseado em um caminho base e o novo atributo. Faz a valida��o se algum dos objetos � nulo para retornar o caminho correto considerando os nulos.
   *
   * @param attribute Nome do atributo a ser anexado.
   * @param basepath Caminho base.
   * @return Caminho Base + . + atributo considerando os nulos e a necessidade de agregar os separadores.
   */
  public static String getAttributePath(String attribute, String basepath) {
    if (basepath != null) {
      if (attribute != null && !"".equals(attribute)) {
        return basepath + "." + attribute;
      } else {
        return basepath;
      }
    } else {
      return attribute;
    }
  }

  /**
   * Monta um caminho baseado em um caminho base e o novo atributo. Faz a valida��o se algum dos objetos � nulo para retornar o caminho correto considerando os nulos.
   *
   * @param attribute Nome do atributo a ser anexado.
   * @param index �ndice do atributo no array.
   * @param basepath Caminho base.
   * @return Caminho Base + . + atributo[index] considerando os nulos e a necessidade de agregar os separadores.
   *
   */
  public static String getAttributePath(String attribute, int index, String basepath) {
    attribute = attribute + "[" + index + "]";
    if (basepath != null) {
      return basepath + "." + attribute;
    } else {
      return attribute;
    }
  }

  /**
   * Procura um RFWVO em uma determinada lista pelo seu ID. <br>
   * Note que se houver mais do que um RFWVO com o mesmo ID, ser� retornado o primeiro encontrado.
   *
   * @param <VO> Tipo do Objeto que extenda {@link RFWVO}
   * @param list Lista de RFWVO
   * @param id ID do VO a ser encontrado.
   * @return Objeto com o ID desejado, ou nulo caso n�o tenha contrado nenhum.
   * @throws RFWException
   */
  public static <VO extends RFWVO> VO getRFWVOByID(List<VO> list, Long id) throws RFWException {
    for (VO vo : list) {
      if (vo.getId().equals(id)) return vo;
    }
    return null;
  }

  /**
   * Busca um "resource" no classPath de acordo com seu nome. Este m�todo tenta encontrar primeiro no mesmo classPath da classe que chamou este m�todo, se n�o encontrar procura no ClassLoader completo.<br>
   * <b>ATEN��O:</B> Este m�todo n�o funciona para resources que estejam dentro de pacotes do tipo EJB/WAR, quando chamado de uma classe de fora. Isso porque o classloader deles � separado por seguran�a. Para ter os resources vis�veis por este m�todo coloque em um Jar "p�blico", como o pacote client, ou um Jar espec�fico para carregar os resources.
   *
   *
   * @param resourceName Nome do Recurso sendo procurado. O nome deve incluir a pasta conforme sua posi��o relativa � raiz. Ex: "resources/img.gif"
   * @return InputStream pronto para ser lido com o conte�do do Resource.
   * @throws RFWException
   */
  public static InputStream getResourceAsStream(String resourceName) throws RFWException {
    InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourceName);
    if (stream == null) {
      try {
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        stream = Class.forName(stack[2].getClassName()).getClassLoader().getResourceAsStream(resourceName);
      } catch (ClassNotFoundException e1) {
        // N�o deve ocorrer pe estamos criando a partir do pr�prio nome da classe, mas logamos
        RFWLogger.logException(e1);
      }
      if (stream == null) {
        final URL url = RUReflex.createURLClassLoader().getResource(resourceName);
        try {
          if (url != null) stream = url.openStream();
        } catch (IOException e) {
          throw new RFWCriticalException("Falha ao abrir o stream da URL do Resource encontrado!", e);
        }
      }
    }
    return stream;
  }

  /**
   * Este m�todo cria um novo ClassLoader de URL com todos os arquivos JARs encontrados no classpath atual.
   *
   * @return ClassLoader com todos os arquivos .jar carregados.
   */
  public static URLClassLoader createURLClassLoader() {
    Collection<String> resources = getResources(Pattern.compile(".*\\.jar"));
    Collection<URL> urls = new ArrayList<>();
    for (String resource : resources) {
      File file = new File(resource);
      // Ensure that the JAR exists and is in the globalclasspath directory.
      if (file.isFile() && "globalclasspath".equals(file.getParentFile().getName())) {
        try {
          urls.add(file.toURI().toURL());
        } catch (MalformedURLException e) {
          // This should never happen.
          e.printStackTrace();
        }
      }
    }
    return new URLClassLoader(urls.toArray(new URL[urls.size()]));
  }

  /**
   * Recupera todos os "resources" do ClassLoader atual baseados em um pattern.
   *
   * @param pattern Express�o Regularar para filtrar os "resources" que ser�o retornados.
   * @return Lista com o URL de todos os recursos encontrados.
   */
  public static Collection<String> getResources(final Pattern pattern) {
    final ArrayList<String> retval = new ArrayList<>();
    final String classPath = System.getProperty("java.class.path", ".");
    final String[] classPathElements = classPath.split(System.getProperty("path.separator"));
    for (final String element : classPathElements) {
      retval.addAll(getResources(element, pattern));
    }
    return retval;
  }

  /**
   * Recupera uma lista de "resources" baseado em um determinado elemento, filtrados por um pattern.
   *
   * @param element URI do Jar ou Diret�rio onde podemos encontrar os "resources".
   * @param pattern Express�o Regular para filtrar os recursos desejados.
   * @return Lista de URI com os "resources" encontrados.
   */
  private static Collection<String> getResources(final String element, final Pattern pattern) {
    final ArrayList<String> retval = new ArrayList<>();
    final File file = new File(element);
    if (file.isDirectory()) {
      retval.addAll(getResourcesFromDirectory(file, pattern));
    } else {
      retval.addAll(getResourcesFromJarFile(file, pattern));
    }
    return retval;
  }

  /**
   * Recupera os "resources" de um diret�rio
   *
   * @param directory Diret�rio para recuperar os "resources"
   * @param pattern Express�o Regulara para filtrar os "resources" retornados.
   * @return Lista com os "resources" encontrados
   */
  private static Collection<String> getResourcesFromDirectory(final File directory, final Pattern pattern) {
    final ArrayList<String> retval = new ArrayList<>();
    final File[] fileList = directory.listFiles();
    for (final File file : fileList) {
      if (file.isDirectory()) {
        retval.addAll(getResourcesFromDirectory(file, pattern));
      } else {
        try {
          final String fileName = file.getCanonicalPath();
          final boolean accept = pattern.matcher(fileName).matches();
          if (accept) {
            retval.add(fileName);
          }
        } catch (final IOException e) {
          throw new Error(e);
        }
      }
    }
    return retval;
  }

  /**
   * Recupera os "resources" de um arquivo JAR.
   *
   * @param file URI do arquivo JAR
   * @param pattern Express�o Regular para filtrar os resultados.
   * @return Lita de Resources encontrados.
   */
  private static Collection<String> getResourcesFromJarFile(final File file, final Pattern pattern) {
    final ArrayList<String> retval = new ArrayList<>();
    ZipFile zf;
    try {
      zf = new ZipFile(file);
    } catch (final ZipException e) {
      throw new Error(e);
    } catch (final IOException e) {
      throw new Error(e);
    }
    final Enumeration<?> e = zf.entries();
    while (e.hasMoreElements()) {
      final ZipEntry ze = (ZipEntry) e.nextElement();
      final String fileName = ze.getName();
      final boolean accept = pattern.matcher(fileName).matches();
      if (accept) {
        retval.add(fileName);
      }
    }
    try {
      zf.close();
    } catch (final IOException e1) {
      throw new Error(e1);
    }
    return retval;
  }

  /**
   * M�todo auxiliar para obter o "�ltimo atributo do caminho".<br>
   *
   * @param path Caminho para extrair o �ltimo bloco.
   * @return Retorna o caminho pai. Ex:
   *         <li>Se passado "a.b.c.d.e" este m�todo deve retornar "e".
   *         <li>Se passado "a" o m�todo deve retornar "a".
   *         <li>Se passado null ou "" retornamos null j� que n�o temos nenhum atributo.
   */
  public static String getLastPath(String path) {
    if (path == null || "".equals(path)) {
      return null;
    }
    int p = path.lastIndexOf(".");
    if (p == -1) {
      return path;
    } else {
      return path.substring(p + 1, path.length());
    }
  }

  /**
   * M�todo auxiliar para obter o "caminho pai".<br>
   *
   * @param path Caminho para extrair o caminho pai.
   * @return Retorna o caminho pai. Ex:
   *         <li>Se passado "a.b.c.d.e" este m�todo deve retornar "a.b.c.d".
   *         <li>Se passado "a" o m�todo deve retornar "" j� que vazio � caminho raiz.
   *         <li>Se passado "" retornamos null j� que n�o temos um caminho pai.
   */
  public static String getParentPath(String path) {
    if (path == null || "".equals(path)) {
      return null;
    }
    int p = path.lastIndexOf(".");
    if (p == -1) {
      return "";
    } else {
      return path.substring(0, p);
    }
  }

  /**
   * Transforma uma Cole��o de GVO em uma Lista contendo todos os VOs internos.
   *
   * @param <VO> Qualquer classe de VO do {@link RFWVO} (extenda {@link RFWVO}).
   * @param vos Array com os {@link GVO}s
   * @return Lista contendo todos os {@link RFWVO}s dos {@link GVO}s recebidos.
   * @throws RFWException
   */
  public static <VO extends RFWVO> List<VO> collectGVOToVOList(Collection<GVO<VO>> vos) throws RFWException {
    return vos.stream().map(GVO::getVO).collect(Collectors.toList());
  }

  /**
   * Verifica se um m�todo � um getter. Um m�todo � considerado um getter se seu nome come�ar com "get", n�o tiver par�metros e retornar um valor.
   *
   * @param method O m�todo a ser verificado.
   * @return true se o m�todo for um getter, false caso contr�rio.
   */
  public static boolean isGetterMethod(Method method) {
    return method.getName().startsWith("get") && method.getParameterCount() == 0 && !method.getReturnType().equals(void.class);
  }

  /**
   * Verifica se um m�todo � um setter. Um m�todo � considerado um setter se seu nome come�ar com "set" e tiver exatamente um par�metro.
   *
   * @param method O m�todo a ser verificado.
   * @return true se o m�todo for um setter, false caso contr�rio.
   */
  public static boolean isSetterMethod(Method method) {
    return method.getName().startsWith("set") && method.getParameterCount() == 1;
  }

  /**
   * Encontra o m�todo setter correspondente a um getter, com base no nome da propriedade e no tipo do par�metro. Ajusta a capitaliza��o do nome da propriedade para seguir as conven��es de m�todos Java.
   *
   * @param clazz A classe que cont�m o m�todo setter.
   * @param propertyName O nome da propriedade (como declarado).
   * @param parameterType O tipo do par�metro que o setter deve aceitar.
   * @return O m�todo setter correspondente, ou null se n�o for encontrado.
   */
  public static Method findSetterMethod(Class<?> clazz, String propertyName, Class<?> parameterType) {
    try {
      String capitalizedPropertyName = RUString.capitalize(propertyName);
      return clazz.getMethod("set" + capitalizedPropertyName, parameterType);
    } catch (NoSuchMethodException e) {
      return null; // Se n�o houver um setter correspondente, retorna null
    }
  }

  /**
   * Encontra o m�todo getter correspondente a um setter, com base no nome da propriedade. Ajusta a capitaliza��o do nome da propriedade para seguir as conven��es de m�todos Java.
   *
   * @param clazz A classe que cont�m o m�todo getter.
   * @param propertyName O nome da propriedade (como declarado).
   * @return O m�todo getter correspondente, ou null se n�o for encontrado.
   */
  public static Method findGetterMethod(Class<?> clazz, String propertyName) {
    try {
      String capitalizedPropertyName = RUString.capitalize(propertyName);
      return clazz.getMethod("get" + capitalizedPropertyName);
    } catch (NoSuchMethodException e) {
      return null; // Se n�o houver um getter correspondente, retorna null
    }
  }

  /**
   * Verifica se uma classe possui tanto um m�todo getter quanto um m�todo setter para uma propriedade espec�fica.
   *
   * @param clazz A classe a ser verificada.
   * @param propertyName O nome da propriedade (como declarado).
   * @param propertyType O tipo do par�metro que o setter deve aceitar.
   * @return true se ambos os m�todos getter e setter existirem, false caso contr�rio.
   */
  public static boolean hasGetterAndSetter(Class<?> clazz, String propertyName, Class<?> propertyType) {
    Method getter = findGetterMethod(clazz, propertyName);
    Method setter = findSetterMethod(clazz, propertyName, propertyType);
    return getter != null && setter != null;
  }

  /**
   * Permite que uma determinada �rvore seja adicionada como parte de outra, adicionando a cada elemento do array uma base inicial da �rvore pai.
   *
   * @param baseprefix
   * @param values
   * @return
   */
  public static String[] addBasePrefix(String baseprefix, String[] values) {
    String[] newvalues = new String[values.length];
    for (int i = 0; i < values.length; i++) {
      newvalues[i] = baseprefix + '.' + values[i];
    }
    return newvalues;
  }

  /**
   * Este m�todo recebe um array de caminhos a serem recuperados (no padr�o do MO do Framework) e filtra apenas os caminhos a partir de um determinado ponto.<br>
   * Exemplo, imagine um objeto:<br>
   * <li>vo
   * <ul>
   * <li>voA
   * <ul>
   * <li>voAA
   * </ul>
   * <ul>
   * <li>voAB
   * </ul>
   * </ul>
   * <ul>
   * <li>voB
   * <ul>
   * <li>voBA
   * </ul>
   * <ul>
   * <li>voBB
   * </ul>
   * </ul>
   * <br>
   * <br>
   *
   * Agora imagine que tenha um array de caminhos para expandir todo o objeto 'vo' e todos os seus filhos:<br>
   * <ul>
   * new String[] { "voA.voAA", "voA.voAB", "voB.voBA", "voB.voBB" };
   * </ul>
   *
   * e que queremos carregar apenas o voB para substituir no objeto principal. Mas como queremos buscar diretamente o objeto voB. Neste caso o array de "exploit" n�o servir� e precisa ser convertido para:<br>
   * <ul>
   * new String[] { "voBA", "voBB" };
   * </ul>
   *
   * Filtrando as entradas que n�o pertencem ao trecho do objeto desejado, e adaptando o caminho para partir apenas do novo objeto.
   *
   * @param tree Array com os caminhos a partir do objeto principal.
   * @param base Caminho base a partir de onde queremos o fazer a busca. No exemplo citado acima seria "voB".
   * @return Retorna a lista de caminhos a partir do ponto desejado, adaptadaos para o trecho em diante. Retorna null caso tenha nenhum resultado no final.
   * @throws RFWException
   */
  public static String[] getSubTree(String[] tree, String base) throws RFWException {
    final ArrayList<String> finalList = new ArrayList<>();

    base = base + '.'; // Faz com que a base termine com '.' por dois motivos: primeiro evita filtrar errado caso o �ltimo atributo da base coincida com o come�o de outro atributo na �rvore. Segundo evita de incluir no filtro quando o caminho da �rvore � exatamente o da base, caso contr�rio teriamos um valor "" adicionado.

    for (int i = 0; i < tree.length; i++) {
      if (tree[i].startsWith(base)) {
        finalList.add(tree[i].substring(base.length()));
      }
    }

    if (finalList.size() == 0) {
      return null;
    } else {
      return finalList.<String> toArray(new String[0]);
    }
  }

  /**
   * Compra dois Objectos (RFWVO) e verifica os valores diferentes recursivamente.
   *
   * @param obj1 Objeto 1 para ser comparado
   * @param obj2 Objeto 2 para ser comparado
   * @return Lista com o caminho das propriedades que apresentaram diferen�as.
   * @throws RFWException
   */
  public static <T extends Object> List<String> compareRecursively(T obj1, T obj2) throws RFWException {
    return compareRecursively(obj1, obj2, null, new LinkedList<Object>());
  }

  /**
   * M�todo auxiliar do {@link #compareRecursively(RFWVO, RFWVO)}
   *
   * @param basepath Caminho dos atributos (recursivo) at� chegarem nesta compara��o. Passar nulo case sejam os dois objetos raiz.
   * @param cache Lista com os objetos que j� foram comparados, evitando assim loop infinito em caso de link c�clico dos objetos. Para o objeto raiz passar uma lista vazia.t� nada
   * @throws RFWException
   */
  private static <T extends Object> List<String> compareRecursively(T obj1, T obj2, String basepath, List<Object> cache) throws RFWException {
    LinkedList<String> atts = new LinkedList<String>();

    if (obj1 == null ^ obj2 == null) {
      atts.add(RUReflex.getAttributePath("", basepath));
    } else if (obj1 != null && obj2 != null) {
      if (!cache.contains(obj1)) {
        cache.add(obj1);
        if (obj1.getClass().isPrimitive() ||
            String.class.isInstance(obj1) ||
            Long.class.isInstance(obj1) ||
            Integer.class.isInstance(obj1) ||
            Boolean.class.isInstance(obj1) ||
            Double.class.isInstance(obj1) ||
            Float.class.isInstance(obj1) ||
            Enum.class.isInstance(obj1) ||
            Date.class.isInstance(obj1) ||
            LocalDate.class.isInstance(obj1) ||
            LocalDateTime.class.isInstance(obj1) ||
            BigDecimal.class.isInstance(obj1) ||
            Class.class.isInstance(obj1)) {
          if (!obj1.equals(obj2)) {
            atts.add(RUReflex.getAttributePath("", basepath));
          }
        } else if (Iterable.class.isInstance(obj1)) {
          Iterator<?> it1 = ((Iterable<?>) obj1).iterator();
          Iterator<?> it2 = ((Iterable<?>) obj2).iterator();
          int index = 0;
          while (true) {
            if (!it1.hasNext() && !it2.hasNext()) {
              break;
            } else if (it1.hasNext() ^ it2.hasNext()) {
              atts.add(RUReflex.getAttributePath("", basepath));
              break;
            } else {
              Object itObj1 = it1.next();
              Object itObj2 = it2.next();
              atts.addAll(compareRecursively(itObj1, itObj2, RUReflex.getAttributePath("", index, basepath), cache));
            }
            index++;
          }
        } else if (Map.class.isInstance(obj1)) {
          Map<?, ?> map1 = (Map<?, ?>) obj1;
          Map<?, ?> map2 = (Map<?, ?>) obj2;
          if (map1.size() != map2.size()) {
            atts.add(RUReflex.getAttributePath("", basepath));
          } else {
            for (Object key : map1.keySet()) {
              Object mapObj1 = map1.get(key);
              Object mapObj2 = map2.get(key);
              if (mapObj1 == null ^ mapObj2 == null) {
                atts.add(RUReflex.getAttributePath("", key, key.getClass(), basepath));
              } else if (mapObj1 != null && mapObj2 != null) {
                atts.addAll(compareRecursively(mapObj1, mapObj2, RUReflex.getAttributePath("", key, key.getClass(), basepath), cache));
              }
            }
          }
        } else if (RFWVO.class.isAssignableFrom(obj1.getClass())) {
          Method[] methods = obj1.getClass().getMethods();
          for (Method method : methods) {
            if ((method.getName().startsWith("get") || method.getName().startsWith("is")) && !method.getName().equals("getClass")) {
              try {
                Object ret1 = method.invoke(obj1);
                Object ret2 = method.invoke(obj2);
                String attribute = null;
                if (method.getName().startsWith("get")) {
                  attribute = method.getName().substring(3, 4).toLowerCase() + method.getName().substring(4);
                } else if (method.getName().startsWith("is")) {
                  attribute = method.getName().substring(2, 3).toLowerCase() + method.getName().substring(3);
                }
                atts.addAll(compareRecursively(ret1, ret2, RUReflex.getAttributePath(attribute, basepath), cache));
              } catch (Exception e) {
                throw new RFWCriticalException("Falha ao comparar semelhan�a dos objetos!", e);
              }
            }
          }
        } else {
          throw new RFWCriticalException("M�todo despreparado para comparar o objeto '" + obj1.getClass().getCanonicalName() + "'.");
        }
      }
    }
    return atts;
  }
}
