package com.globalegrow.util;

import org.apache.commons.mail.EmailException;  
import org.apache.commons.mail.HtmlEmail;  
import org.apache.log4j.Logger;  
   
/**  
 * 邮件发送工具实现类   
 * @author linchaojiang
 * @create 2017-02-15 
 */  
public class MailUtil {  
  
    protected final Logger logger = Logger.getLogger(getClass());  
  
    /**
     * 发送电子邮件
     * @param mail
     * @return
     * @create 2017-02-15,update:2017-02-15
     */
    public boolean send(Mail mail) {  
        // 发送email  
        HtmlEmail email = new HtmlEmail();  
        try {  
            email.setHostName(mail.getHost());// 这里是SMTP发送服务器的名字：163的如下："smtp.163.com"  
            email.setCharset(Mail.ENCODEING); // 字符编码集的设置   
            email.setFrom(mail.getSender(), mail.getName()); // 发送人的邮箱   
            //如果需要认证信息的话，设置认证：用户名-密码。分别为发件人在邮件服务器上的注册名称和密码  
            email.setAuthentication(mail.getUsername(), mail.getPassword());  
            email.addTo(mail.getReceivers()); // 收件人的邮箱 
            if(mail.getCcaddress().length>0){
            	email.addCc(mail.getCcaddress());//添加抄送人地址
            }           
            //email.addBcc(mail.getBccaddress());//添加密送人地址
            //email.addReplyTo(mail.getReplyaddress());//添加回复人地址 
            email.setSubject(mail.getSubject());  // 要发送的邮件主题  
            email.setMsg(mail.getMessage());// 要发送的信息，由于使用了HtmlEmail，可以在邮件内容中使用HTML标签 
            email.send();// 发送  
            if (logger.isDebugEnabled()) {  
                logger.debug(mail.getSender() + " 发送邮件到 " + mail.getReceiver());  
            }  
            return true;  
        } catch (EmailException e) {  
            e.printStackTrace();  
            logger.info(mail.getSender() + " 发送邮件到 " + mail.getReceiver()  + " 失败");  
            return false;  
        }  
    }  
} 