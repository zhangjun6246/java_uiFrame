package com.globalegrow.util;

import java.io.Serializable;  

/**  
 * Mail属性实体  
 * @author linchaojiang  
 *   
 */  
@SuppressWarnings("serial")  
public class Mail implements Serializable {  
  
    public static final String ENCODEING = "UTF-8";  
  
    private String host; // 服务器地址   
    private String sender; // 发件人的邮箱   
    private String receiver; // 收件人的邮箱  
    private String name; // 发件人昵称  
    private String username; // 账号  
    private String password; // 密码  
    private String subject; // 主题  
    private String message; // 信息(支持HTML)  
    private String[] receivers;//收件人地址
    private String[] ccaddress;//抄送地址
    private String[] bccaddress;//密送地址
    private String replyaddress;//回复地址
  
    public String getHost() {  
        return host;  
    }  
  
    public void setHost(String host) {  
        this.host = host;  
    }  
  
    public String getSender() {  
        return sender;  
    }  
  
    public void setSender(String sender) {  
        this.sender = sender;  
    }  
  
    public String getReceiver() {  
        return receiver;  
    }  
  
    public void setReceiver(String receiver) {  
        this.receiver = receiver;  
    }  
  
    public String getName() {  
        return name;  
    }  
  
    public void setName(String name) {  
        this.name = name;  
    }  
  
    public String getUsername() {  
        return username;  
    }  
  
    public void setUsername(String username) {  
        this.username = username;  
    }  
  
    public String getPassword() {  
        return password;  
    }  
  
    public void setPassword(String password) {  
        this.password = password;  
    }  
  
    public String getSubject() {  
        return subject;  
    }  
  
    public void setSubject(String subject) {  
        this.subject = subject;  
    }  
  
    public String getMessage() {  
        return message;  
    }  
 
    public void setMessage(String message) {  
        this.message = message;  
    }

	public String[] getReceivers() {
		return receivers;
	}

	public void setReceivers(String[] receivers) {
		this.receivers = receivers;
	}

	public String[] getCcaddress() {
		return ccaddress;
	}

	public void setCcaddress(String[] ccaddress) {
		this.ccaddress = ccaddress;
	}

	public String[] getBccaddress() {
		return bccaddress;
	}

	public void setBccaddress(String[] bccaddress) {
		this.bccaddress = bccaddress;
	}

	public String getReplyaddress() {
		return replyaddress;
	}

	public void setReplyaddress(String replyaddress) {
		this.replyaddress = replyaddress;
	}  
  
}  