package utils;
import java.math.BigDecimal;
import java.text.DecimalFormat;

public class AmountUtil {   
	
	public static String getCapitalAmount(BigDecimal inAmount) throws Exception{
		BigDecimal amount = new BigDecimal(0);
		BigDecimal zero = new BigDecimal("0.00").setScale(2, BigDecimal.ROUND_HALF_UP);   
		if(inAmount.compareTo(zero) == -1){
			amount = inAmount.negate();
		}else{
			amount = inAmount;
		}
	    amount = amount.setScale(2, BigDecimal.ROUND_HALF_UP);    
	    /**1 处理特殊的*/  
	    if(zero.compareTo(amount) == 0 )return "零元整";   
	    //最多不能超过999999999999（千亿）   
	    if(amount.compareTo(new BigDecimal("999999999999")) >0){
	    	throw new Exception("金额超过千亿，不能正常转换");
	    };
	    String str = "";   
	    String number[] = new String[]{"零 ","壹 ","贰 ","叁 ","肆 ","伍 ","陆 ","柒 ","捌 ","玖 " };   
	    String units[] = new String[]{"元","拾","佰","仟","万","拾","佰","仟","亿","拾","佰","仟",};   
	    double temp = amount.doubleValue();   
	    int i = 0;   
	    /**2 整数部分，按从小到大的顺序把各位拆分开*/  
	    while(temp >= 10){   
	        int n = (int)(temp % 10);   
	        temp = temp / 10L;   
	        str = str.trim()+units[i++].trim()+number[n].trim();   
	    }   
	    str = str.trim()+units[i++].trim()+number[(int)temp].trim();   
	    StringBuffer sb = new StringBuffer(str.trim());    
	    /**3 反转成正常的顺序*/  
	     sb.reverse();   
	     str = sb.toString();   
	     /**4 处理小数部分*/  
	     int dolt1 =(int)( amount.multiply(new BigDecimal("100")).longValue()%10);//分   
	     int dolt2 =(int)( amount.multiply(new BigDecimal("10")).longValue()%10);//角   
	     if(dolt1 == 0 && dolt2 == 0){str = str +"整";}   
	     else if(dolt1 != 0){    
	         if(dolt2 != 0)  str = str + number[dolt2].trim()+"角"+number[dolt1].trim()+"分";   
	         else str = str + "零"+number[dolt1].trim()+"分";   
	    }else if(dolt2 != 0){ str = str + number[dolt2].trim()+"角整";}   
	     /**5 去除多余的汉字并在合适的位置加上零*/  
	    while(str.indexOf("零亿")!= -1 ||str.indexOf("零万")!= -1 ||str.indexOf("零仟")!= -1    
	            ||str.indexOf("零佰")!= -1 ||str.indexOf("零拾")!= -1  
	            ||str.indexOf("零元")!= -1 ){   
	        str = str.replaceAll("零亿", "亿");   
	        str = str.replaceAll("零万", "万");   
	        str = str.replaceAll("零仟", "零");   
	        str = str.replaceAll("零佰", "零");   
	        str = str.replaceAll("零拾", "零");   
	        if(str.startsWith("零元")) str = str.replaceAll("零元", "");   
	        else str = str.replaceAll("零元", "元");   
	    }   
	    /**6 去掉零以后可能出现亿万，万千连着的情况，要去掉如800000000，原来为 捌亿万元，要变成 捌亿零元*/  
	    if(str.indexOf("亿万") != -1){   
	        str = str.replaceAll("亿万", "亿零");   
	    }   
	    if(str.indexOf("万仟") != -1){   
	        str = str.replaceAll("万仟", "万零");   
	    }   
	    /**7 万位、亿位是"0"，但千位不是"0"时，金额中写一个零字  
	        如69906711.62，原来为 陆仟玖佰玖拾万陆仟柒佰壹拾壹元陆角贰分  
	        要写成 陆仟玖佰玖拾万零陆仟柒佰壹拾壹元陆角贰分*/  
	    String req = ".*佰亿.仟.*";   
	    if(str.matches(req)){   
	        str = str.substring(0,str.indexOf("佰亿")+2)+ "零" +str.substring(str.indexOf("佰亿")+2);   
	    }   
	    req = ".*拾亿.仟.*";   
	    if(str.matches(req)){   
	        str = str.substring(0,str.indexOf("拾亿")+2)+ "零" +str.substring(str.indexOf("拾亿")+2);   
	    }   
	    req = ".*佰万.仟.*";   
	    if(str.matches(req)){   
	        str = str.substring(0,str.indexOf("佰万")+2)+ "零" +str.substring(str.indexOf("佰万")+2);   
	    }   
	    req = ".*拾万.仟.*";   
	    if(str.matches(req)){   
	        str = str.substring(0,str.indexOf("拾万")+2)+ "零" +str.substring(str.indexOf("拾万")+2);   
	    }   
	    /**8 如果有零元的变成元*/  
	    while(str.indexOf("零元") != -1){   
	        str = str.replaceAll("零元", "元");   
	    }   
	    /**9 如果元位为0，但是后面有角，元和角之间加上零*/  
	    if((str.indexOf("拾元")!= -1 ||str.indexOf("佰元")!= -1 ||str.indexOf("仟元")!= -1    
	            ||str.indexOf("万元")!= -1 ||str.indexOf("亿元")!= -1 ) && str.indexOf("角") != -1 ){   
	        str = str.substring(0,str.indexOf("元")+1)+ "零" +str.substring(str.indexOf("元")+1);   
	    }   
	    /**10 去掉重复的零*/  
	    while(str.indexOf("零零") != -1){   
	        str = str.replaceAll("零零", "零");   
	    }   
	    /**11 处理一些特殊情况*/    
	    if((str.startsWith("整") || str.startsWith("零"))&&str.length()>1 ){   
	        str =  str.substring(1);   
	    }
	    if(inAmount.compareTo(zero) == -1){
	    	str = "负"+str;
		}
	    return str;   
	}
	/**
	 * 将金额装换成111,111.00的形式
	 * @param inAmount
	 * @return
	 * @throws Exception
	 */
	public static String amountFormate(BigDecimal inAmount){
		String pattern = ",##0.00";
		String str = "";
		if(null != inAmount){
			DecimalFormat de = new DecimalFormat(pattern);
			str = de.format(inAmount.setScale(2, BigDecimal.ROUND_HALF_UP));
		}
	    return str;
    }
    /**
     * 将金额装换成111111.00的形式
     * @param bd
     * @return
     */
    public static String decimalFormat(BigDecimal bd) {
        if (bd == null) {
            bd=new BigDecimal( "0");
        }
        DecimalFormat df = new DecimalFormat("########0.00");
        String str = df.format(bd.setScale(2, BigDecimal.ROUND_HALF_UP));
        return str;
    }
    public static BigDecimal decimalFormat(BigDecimal pricea, BigDecimal priceb) {
        if (pricea == null) {
            pricea = new BigDecimal("0");
        }
        if (priceb == null) {
            priceb = new BigDecimal("0");
        }

        BigDecimal bigDecimal = pricea.subtract(priceb);
        return bigDecimal;
    }
	/**
	 * 将金额装换成111,111.00的形式
	 * @param inAmount
	 * @return
	 * @throws Exception
	 */
	public static String toString(BigDecimal inAmount){
		String str = "";
		if(null != inAmount){
			str  = inAmount.toString();
		}
	    return str;   
	}
	
	public static String subtraction(BigDecimal a,BigDecimal b){
		return amountFormate(a.subtract(b));
	}
	
	public static String toFei(BigDecimal yuan){
		String str = "";
		if(null!=yuan){
			str = yuan.multiply(new BigDecimal(100)).setScale(0, BigDecimal.ROUND_HALF_UP).toString();
		}
		return str;
	}
	public static boolean isNumber(String str){
		if(str != null){
			try {
				Double.parseDouble(str);
				return true;
			} catch (NumberFormatException e) {
				//校验失败
			}
		}
		return false;
	}
	/**
	 * 
	 * @param bd
	 * @return
	 */
	public static String round(BigDecimal bd) {
        if (bd == null) {
        	bd = BigDecimal.ZERO;
        }
        return bd.setScale(2, BigDecimal.ROUND_HALF_UP).toString();
    }
	/**
	 * 
	 * @param bd
	 * @return
	 */
	public static BigDecimal round2(BigDecimal bd) {
        if (bd == null) {
        	bd = BigDecimal.ZERO;
        }
        return bd.setScale(2, BigDecimal.ROUND_HALF_UP);
    }
}  
