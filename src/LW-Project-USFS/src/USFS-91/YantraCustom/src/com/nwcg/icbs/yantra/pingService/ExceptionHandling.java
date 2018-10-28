package com.nwcg.icbs.yantra.pingService;

/*
import com.ibm.mq.MQException;
import com.nwcg.icbs.yantra.util.common.NWCGROSSLogger;
import com.yantra.yfc.log.YFCLogCategory;

import java.io.PrintStream;
import java.lang.reflect.Field;
*/
public class ExceptionHandling
{
	/*
  static int titleSize = 30;
  private static String lineSeparator = System.getProperty("line.separator");
  private static YFCLogCategory logger = NWCGROSSLogger.instance(ExceptionHandling.class);
  
  private static String padTitle(String text)
  {
    StringBuffer buffer = new StringBuffer(titleSize);
    int textLength = text.trim().length();
    
    int padding = titleSize - textLength;
    for (int count = 0; count < padding; count++) {
      buffer.append(" ");
    }
    buffer.append(text.trim()).append(" : ");
    
    return buffer.toString();
  }
  
  public static void handleException(Exception exception)
  {
    logger.verbose("------------------------------HANDLING EXCEPTION------------------------------",exception);
    PrintStream stream = System.out;
    
    int causeIndex = 0;
    Throwable loopException = exception;
    while (loopException != null)
    {
      augmentStackTrace(loopException);
      if (causeIndex == 0) {
        stream.println(padTitle("Message") + loopException.getMessage());
      } else {
        stream.println(padTitle(new StringBuilder("Caused by [").append(causeIndex).append("] --> Message").toString()) + 
          loopException.getMessage());
      }
      stream.println(padTitle("Class") + loopException.getClass());
      if ((loopException instanceof MQException))
      {
        MQException mqException = (MQException)loopException;
        stream.println(padTitle("Comp code") + mqException.completionCode);
        stream.println(padTitle("Reason code") + mqException.reasonCode);
      }
      dumpStackTrace(loopException, stream);
      
      loopException = loopException.getCause();
      causeIndex++;
    }
    logger.verbose("------------------------------HANDLED EXCEPTION------------------------------");
  }
  
  private static void dumpStackTrace(Throwable e, PrintStream stream)
  {
    StackTraceElement[] stack = e.getStackTrace();
    for (int count = 0; count < stack.length; count++)
    {
      String prefix;
      if (count == 0) {
        prefix = "Stack";
      } else {
        prefix = "";
      }
      String className = stack[count].getClassName();
      String methodName = stack[count].getMethodName();
      String fileName = stack[count].getFileName();
      int line = stack[count].getLineNumber();
      
      stream.println(padTitle(prefix) + className + "." + methodName + "(" + fileName + ":" + line + 
        ")");
    }
  }
  
  public static void augmentStackTrace(Throwable t)
  {
    StackTraceElement[] elements = t.getStackTrace();
    for (int i = 0; i < elements.length; i++)
    {
      StackTraceElement element = elements[i];
      String className = element.getClassName();
      try
      {
        Class c = Class.forName(className);
        Field[] fields = c.getDeclaredFields();
        Field field = null;
        for (int f = 0; f < fields.length; f++)
        {
          field = fields[f];
          if (field.getName().startsWith("sccs")) {
            break;
          }
        }
        if (field == null) {
          continue;
        }
        field.setAccessible(true);
        


        Object info = field.get(null);
        String sccs;
        if (info != null)
        {
          String[] infos = info.toString().split(" ");
          sccs = "[" + infos[4] + " " + infos[5] + "](";
        }
        else
        {
          sccs = "[n.a]";
        }
        elements[i] = new StackTraceElement(element.getClassName(), element.getMethodName(), sccs + element.getFileName(), element.getLineNumber());
      }
      catch (Exception localException) {}
      t.setStackTrace(elements);
    }
  }
  */
}

