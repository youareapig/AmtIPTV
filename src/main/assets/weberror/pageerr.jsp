<html>
<!-- HTTP错误码页面 -->
<style type="text/css">
* {
	font-family: "SimHei";
	color: #FFFFFF;
}

body {
	margin: 0px;
	padding: 0px;
}

.noticecontent1 {
	font-size: 16px;
}

.notice_error_code {
	font-size: 14px;
}
</style>

<head>
<title></title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
</head>

<script type="text/javascript">

	var pageSize = "SD";
	function init() {
		var width = Navigation.getPageViewWidth();
		console.log("Page width: " + width);
		if (width >= 1000) {
			pageSize = "HD";
		}
		if (pageSize == "HD") {
			document.getElementById("body").style = "width:1280px; height:720px; position:absolute; padding:25px; ";
			document.getElementById("body").style.backgroundImage="url(file:///android_asset/weberror/images/bg-cominfodisplay.jpg)";
			document.getElementById("divTitle").style.fontSize ="22px"; 
			document.getElementById("content").style.fontSize ="22px";
			document.getElementById("table").style.height = "720px";
		} else {
			document.getElementById("body").style ="width: 640px; height:530px; position: absolute;";
			document.getElementById("body").style.backgroundImage="url(file:///android_asset/weberror/images/bg-cominfodisplay3.jpg)";
			document.getElementById("table").style.height = "530px";
				}
	}

    document.onirkeypress = grabEvent;
	document.onkeypress = grabEvent;
    
  	function grabEvent(e)
	{
		var et = typeof e=="undefined" ? event : e ;
		var val = et.which ? et.which : et.keyCode;
        return keypress(val);
    }
    
    /**
     *返回到上一级页面   有个问题：返回到上一个页面时，如果上一个页面没有设置分辨率(高清)，会导致页面变大   */
    function goBack()
    {
		var EPGDomain = Authentication.CTCGetConfig("EPGDomain");
       console.log("error.jsp-->EPGDomain:" + EPGDomain);
       window.location = EPGDomain;
       //Utility.setValueByName('SMALL_SCREEN','');
    }
    
    function keypress(keyval)
    {
    	EPGMain.LogMsg("error.jsp-->keycode:"+keyval);
        switch(keyval)
        {
            case 283://回退键和返回键同样处理         
            case 315:
			case 8:
			case 272:
                goBack();
                return 0;//返回0机定盒不处理
            default :
                break;
        }
        return 1;
    }
-->
</script>

<!-- file:///android_asset/weberror/images -->
<body onload="init()">

	<div id="body">

		<table  id ="table"  align="center">
			<tr>
				<td valign="middle" style="height: 526px;">
					<!-- 错误标题-->
					<div id="divTitle" class="noticecontent1"></div> <!-- 错误码相关 -->
					<div id="content" class="notice_error_code">
						<br />错误代码：<span id="divErrorCode"><errcode>
						</span><span id="spanErrorInfo">错误信息：<span id="divErrorInfo"></span></span>
					</div> <!-- 操作建议 -->
					<div id="divSuggest" class="notice_error_code"
						style="margin-top: 5px;"></div>
				</td>
			</tr>
		</table>

	</div>
</body>
</html>

<script type="text/javascript">

//走分支
var plattype = "";
try{plattype = EPGMain.getConfigPLATTYPE();}catch(e){}


//错误消息对象
function ErrorObj(){
	this.title="";
	this.errorCode="";
	this.errorInfo="";
	this.suggest="";
	this.isdeal=false;//分支是否已经处理
}

//默认的提示
var Default = {
	init : function(errorCode, errorObj){
		if("10071" == errorCode){//页面访问超时无响应
			errorObj.errorCode = errorCode;
			errorObj.errorInfo="页面访问超时无响应。";
			errorObj.suggest="请返回首页观看其他节目，若依然失败，请按照以下步骤尝试解决故障：<br/>1、请尝试断电重启机顶盒；<br/>2、请检查接入网络是否正常；<br/>3、请断电重启modem或路由器；<br/>4、若按以上操作，问题仍未解决，请拨打运营商客户服务热线进行咨询。";			
		}else{
			
			errorObj.title="您所观看的节目正在维护中，请返回首页观看其他节目。";
			var errorInfo="";
			if(errorCode && errorCode != "" && !isNaN(errorCode))//http状态错误
			{
				errorCode = parseInt(errorCode);
				
				if(errorCode>=400 && errorCode<=505){ 

					switch(errorCode){
					case 400:
						errorInfo="Bad";
					break;
					case 401:
						errorInfo="Unauthorized";
					break;
					case 402:
						errorInfo="Payment Required";
					break;
					case 403:
						errorInfo="Forbidden";
					break;
					case 404:
						errorInfo="Not Found";
					break;
					case 405:
						errorInfo="Method Not Allowed";
					break;
					case 406:
						errorInfo="Not Acceptable";
					break;
					case 407:
						errorInfo="Proxy Authentication Required";
					break;
					case 408:
						errorInfo="Request Timeout";
					break;
					case 410:
						errorInfo="Gone";
					break;
					case 411:
						errorInfo="Length Required";
					break;
					case 412:
						errorInfo="Precondition Failed";
					break;
					case 413:
						errorInfo="Request Entity Too Large";
					break;
					case 414:
						errorInfo="Request-URI Too Long";
					break;
					case 415:
						errorInfo="Unsupported Media Type";
					break;
					case 416:
						errorInfo="Requested Range Not Satisfiable";
					break;
					case 417:
						errorInfo="Expectation Failed";
					break;
					case 500:
						errorInfo="Internal Server Error";
					break;
					case 501:
						errorInfo="Not Implemented";
					break;
					case 502:
						errorInfo="Bad Gateway";
					break;
					case 503:
						errorInfo="Service Unavailable";
					break;
					case 504:
						errorInfo="Gateway Timeout";
					break;
					case 505:
						errorInfo="HTTP Version Not Supported";
					break;
					}
					
					errorCode = "HTTP "+ errorCode;
				}
			}else
				errorCode="UnKnow";
			
			errorObj.errorCode = errorCode;
			errorObj.errorInfo = errorInfo;
		}
	}
}

//上海
var SHGQ = {
	init : function(errorCode, errorObj){
		errorObj.isdeal = true;
		if("10071" == errorCode){//页面访问超时无响应
			errorObj.errorCode = "0027";
			errorObj.title="服务连接失败。";
			errorObj.errorInfo="终端访问EPG服务器超时";
			errorObj.suggest="操作建议：按  “IPTV” 进入IPTV首页，观看IPTV业务，或拨打运营商客服电话";			
		}else
		{
			if(errorCode && errorCode != "" && !isNaN(errorCode))//http状态错误
			{
				var errorInfo="";
				errorCode = parseInt(errorCode);
				if(errorCode>=400 && errorCode<=505){//HTTP
					errorCode = "H "+ errorCode;
					errorObj.suggest="操作建议：按“首页”或“云应用”按键，进入首页或桌面";
					errorObj.errorCode = errorCode;
					errorObj.errorInfo = errorInfo;
					return;
				}
			}
			errorObj.isdeal = false;
		}
	}
}
	

</script>

<script type="text/javascript">
	function request(paras) {
		var url = location.href;
		var paraString = url.substring(url.indexOf("?") + 1, url.length).split(
				"&");
		var paraObj = {};
		for (i = 0; j = paraString[i]; i++) {
			paraObj[j.substring(0, j.indexOf("=")).toLowerCase()] = j
					.substring(j.indexOf("=") + 1, j.length);
		}
		var returnValue = paraObj[paras.toLowerCase()];
		if (typeof (returnValue) == "undefined") {
			return "";
		} else {
			return returnValue;
		}
	}

	var divTitle = document.getElementById("divTitle");
	var divSuggest = document.getElementById("divSuggest");
	var divErrorCode = document.getElementById("divErrorCode");
	var divErrorInfo = document.getElementById("divErrorInfo");
	
	divTitle.innerHTML=""; 
	divErrorInfo.innerHTML="";
	divSuggest.innerHTML="";

	var errorCode = request("code");
	if(!errorCode)
		errorCode = divErrorCode.innerHTML;
	
	var errorInfo = unescape(request("desc"));
	try{EPGMain.LogMsg("page-errorCode-->"+errorCode+",desc:"+errorInfo);}catch(e){}

	var errorObj = new ErrorObj();	
	if("SHGQ"==plattype)
		SHGQ.init(errorCode,errorObj);
		
	if(!errorObj.isdeal){//分支未处理，则进入默认
		Default.init(errorCode,errorObj);
	}

	//显示
	divTitle.innerHTML=errorObj.title;
	divErrorCode.innerHTML=errorObj.errorCode;
	
	divErrorInfo.innerHTML= errorObj.errorInfo == "" ? errorInfo : errorObj.errorInfo ;
	if(divErrorInfo.innerHTML){
		document.getElementById("spanErrorInfo").style.display="block";
	}else
		document.getElementById("spanErrorInfo").style.display="none";
	
	divSuggest.innerHTML=errorObj.suggest;	
	
	//上报,通知JAVA
	if(errorObj.errorCode){
		errorObj.errorCode = errorObj.errorCode.replace("HTTP ","").replace("H ","").replace("R ","");
		if(!isNaN(errorObj.errorCode))
			try{EPGMain.set("ShowErrorPage-Code",errorObj.errorCode);}catch(e){}
	}
	try{EPGMain.set("ShowErrorPage","1");}catch(e){} 
	 
</script>

