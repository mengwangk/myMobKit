angular.module('templates-app', ['about/about.tpl.html', 'call/call.tpl.html', 'camera/camera.tpl.html', 'contact/contact.tpl.html', 'controlpanel/controlpanel.tpl.html', 'device/device.tpl.html', 'fullscreen/fullscreen.tpl.html', 'gateway/gateway.tpl.html', 'home/home.tpl.html', 'image/image.tpl.html', 'location/location.tpl.html', 'login/login.tpl.html', 'media/media.tpl.html', 'messaging/messaging.tpl.html', 'mms/mms.tpl.html', 'notif/notif.tpl.html', 'sensor/sensor.tpl.html', 'services/services.tpl.html', 'surveillance/surveillance.tpl.html', 'tracker/tracker.tpl.html', 'ussd/ussd.tpl.html', 'vcalendar/vcalendar.tpl.html', 'vcard/vcard.tpl.html', 'video/video.tpl.html', 'viewer/viewer.tpl.html']);

angular.module("about/about.tpl.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("about/about.tpl.html",
    "<div class=\"container\">\n" +
    "	\n" +
    "	<p>\n" +
    "		<a href=\"http://www.mymobkit.com\"><img class=\"img-responsive\" src='assets/images/mymobkit.png'/>myMobKit</a> is an\n" +
    "		Android application that can be used to provide extended functions to\n" +
    "		your smart phone. Development is currently in progress to add additional features to \n" +
    "		the app. Currenty the following features are supported.\n" +
    "	</p>\n" +
    "\n" +
    "	<p>\n" +
    "		<ul style=\"margin-left:40px\">\n" +
    "			<li style=\"list-style:circle\">Built-in web server to access your phone.</li>\n" +
    "			<li style=\"list-style:circle\">Use your phone camera for surveillance.</li>\n" +
    "			<li style=\"list-style:circle\">Remotely view and control your phone through the web control panel.</li>\n" +
    "			<li style=\"list-style:circle\">Remotely start up surveillance feature using SMS.</li>\n" +
    "			<li style=\"list-style:circle\">Control access to the app through lock pattern.</li>\n" +
    "			<li style=\"list-style:circle\">Motion detection and face detection using different algorithms.</li>\n" +
    "			<li style=\"list-style:circle\">Trigger alarm through SMS or email at configured interval and triggers.</li>\n" +
    "			<li style=\"list-style:circle\">Save detected images to cloud storage or attach in email.</li>\n" +
    "			<li style=\"list-style:circle\">Ability to disguise the camera and close the app when device is touched.</li>\n" +
    "			<li style=\"list-style:circle\">Turn your mobile phone into a SMS gateway, exposing the functions through REST APIs.</li>\n" +
    "		</ul>\n" +
    "	</p>\n" +
    "\n" +
    "</div>\n" +
    "\n" +
    "");
}]);

angular.module("call/call.tpl.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("call/call.tpl.html",
    "<div class=\"container-fluid\">\n" +
    "	<div class=\"row\">\n" +
    "		<div class=\"col-md-3\" id=\"sidebar\" ng-include=\"'assets/view/services_sidebar.html'\"></div>\n" +
    "		\n" +
    "		\n" +
    "		<div class=\"col-md-9\" id=\"content\">\n" +
    "			\n" +
    "			<div class=\"alert alert-info\">\n" +
    "				<h3 class=\"alert-heading\">Call API</h3>\n" +
    "			</div>\n" +
    "						\n" +
    "			<br/>\n" +
    "			\n" +
    "			<div class=\"alert alert-success\">				\n" +
    "				<h4 class=\"alert-heading\"> <button class=\"btn btn-default btn-xs\">Post</button> Make Call</h4>\n" +
    "				<br/>\n" +
    "				<p class=\"long-wrap\">\n" +
    "				{{callLink}}\n" +
    "				</p>\n" +
    "			</div>\n" +
    "			\n" +
    "			<table id=\"sms_api_doc\" class=\"table table-striped\">\n" +
    "				<thead>\n" +
    "					<tr>\n" +
    "						<th>Parameter</th>\n" +
    "						<th>Description</th>\n" +
    "					</tr>\n" +
    "				</thead>\n" +
    "				\n" +
    "				<tbody>    	      \n" +
    "					<tr id=\"Command\">\n" +
    "						<td>\n" +
    "							Destination\n" +
    "						</td>\n" +
    "						<td>\n" +
    "							Mandatory parameter. The contact name or phone number to call.\n" +
    "						</td>\n" +
    "					</tr>					\n" +
    "					\n" +
    "				</tbody>\n" +
    "			</table>\n" +
    "			\n" +
    "			\n" +
    "			<div class=\"alert alert-warning\">\n" +
    "				<h4 class=\"alert-heading\">Response</h4>\n" +
    "				<p class=\"long-wrap\">\n" +
    "				<code>\n" +
    "				{\n" +
    "				    \"destination\": \"+60123456789\",\n" +
    "				    \"description\": \"\",\n" +
    "				</code>\n" +
    "				<br/>\n" +
    "				<code>\n" +
    "				    \"requestMethod\": \"POST\",\n" +
    "				    \"isSuccessful\": true\n" +
    "				}\n" +
    "				</code>\n" +
    "				</p>\n" +
    "			</div>\n" +
    "\n" +
    "			<table id=\"sms_api_doc\" class=\"table table-striped\">\n" +
    "			<thead>\n" +
    "				<tr>\n" +
    "					<th>Field</th>\n" +
    "					<th>Description</th>\n" +
    "				</tr>\n" +
    "			</thead>\n" +
    "			\n" +
    "			<tbody>   \n" +
    "				<tr id=\"destination\">\n" +
    "					<td>\n" +
    "						destination\n" +
    "					</td>\n" +
    "				<td>\n" +
    "					Destination number to call.\n" +
    "				</td>\n" +
    "				</tr> 	      \n" +
    "				<tr id=\"description\">\n" +
    "					<td>\n" +
    "						description\n" +
    "					</td>\n" +
    "				<td>\n" +
    "					Contains the error message if isSuccessful is \"false\".\n" +
    "				</td>\n" +
    "				</tr>\n" +
    "				\n" +
    "				<tr id=\"requestMethod\">\n" +
    "					<td>\n" +
    "						requestMethod\n" +
    "					</td>\n" +
    "				<td>\n" +
    "					Request method. Default to \"GET\", \"POST\" or \"DELETE\" depending on the HTTP method.\n" +
    "				</td>\n" +
    "				</tr>\n" +
    "				\n" +
    "				<tr id=\"isSuccessful\">\n" +
    "					<td>\n" +
    "						isSuccessful\n" +
    "					</td>\n" +
    "				<td>\n" +
    "					true if operation is successful. Otherwise returns false.\n" +
    "				</td>\n" +
    "				</tr>\n" +
    "				\n" +
    "				\n" +
    "			</tbody>\n" +
    "			</table>\n" +
    "\n" +
    "			<br/><br/>\n" +
    "\n" +
    "				<div class=\"alert alert-success\">				\n" +
    "				<h4 class=\"alert-heading\"> <button class=\"btn btn-default btn-xs\">Get</button> Retrieve Call Log</h4>\n" +
    "				<br/>\n" +
    "				<p class=\"long-wrap\">\n" +
    "				<a href=\"{{callLink}}\">{{callLink}}</a></b></b>\n" +
    "				</p>\n" +
    "			</div>\n" +
    "					\n" +
    "			\n" +
    "			<div class=\"alert alert-warning\">\n" +
    "				<h4 class=\"alert-heading\">Response</h4>\n" +
    "				<p class=\"long-wrap\">\n" +
    "				<code>\n" +
    "				\n" +
    "					{\n" +
    "					    \"callHistories\": [\n" +
    "					    </code>\n" +
    "						<br/>\n" +
    "						<code>\n" +
    "					        {\n" +
    "					            \"callDuration\": \"7\",\n" +
    "					            \"callTime\": \"2015-10-19T20:08:28GMT+08:00\",\n" +
    "					     </code>\n" +
    "						<br/>\n" +
    "						<code>\n" +
    "					            \"callType\": \"1\",\n" +
    "					            \"phoneNumber\": \"0126868739\"\n" +
    "					        },\n" +
    "					    </code>\n" +
    "						<br/>\n" +
    "						<code>\n" +
    "					        {\n" +
    "					            \"callDuration\": \"43\",\n" +
    "					            \"callTime\": \"2015-10-19T23:35:56GMT+08:00\",\n" +
    "					      </code>\n" +
    "						<br/>\n" +
    "						<code>\n" +
    "					            \"callType\": \"2\",\n" +
    "					            \"phoneNumber\": \"0320535153\"\n" +
    "					        },\n" +
    "					      </code>\n" +
    "						<br/>\n" +
    "						<code>\n" +
    "					        {\n" +
    "					            \"callDuration\": \"0\",\n" +
    "					            \"callTime\": \"2015-10-20T11:15:07GMT+08:00\",\n" +
    "					            \"callType\": \"3\",\n" +
    "					       </code>\n" +
    "						<br/>\n" +
    "						<code>\n" +
    "					            \"phoneNumber\": \"0326015000\"\n" +
    "					        }\n" +
    "							    ],\n" +
    "						  </code>\n" +
    "						<br/>\n" +
    "						<code>\n" +
    "							    \"description\": \"\",\n" +
    "							    \"requestMethod\": \"GET\",\n" +
    "							    \"isSuccessful\": true\n" +
    "						}\n" +
    "						</code>\n" +
    "				</p>\n" +
    "			</div>\n" +
    "\n" +
    "			<table id=\"sms_api_doc\" class=\"table table-striped\">\n" +
    "			<thead>\n" +
    "				<tr>\n" +
    "					<th>Field</th>\n" +
    "					<th>Description</th>\n" +
    "				</tr>\n" +
    "			</thead>\n" +
    "			\n" +
    "			<tbody>    	      \n" +
    "\n" +
    "				<tr id=\"callHistories\">\n" +
    "					<td>\n" +
    "						callHistories\n" +
    "					</td>\n" +
    "					\n" +
    "					<td>\n" +
    "						Call histories \n" +
    "						<br/>						\n" +
    "						<dl class=\"dl-horizontal\">\n" +
    "						  <dt>callDuration</dt>\n" +
    "						  <dd>The duration of the call in seconds.</dd>\n" +
    "						  <dt>callTime</dt>\n" +
    "						  <dd>The date the call occured.</dd>\n" +
    "						  <dt>callType</dt>\n" +
    "						  <dd>The type of the call (incoming, outgoing or missed).\n" +
    "						  <br/>Incoming - 1, Outgoing - 2, Missed - 3\n" +
    "						  </dd>\n" +
    "						  <dt>phoneNumber</dt>\n" +
    "						  <dd>The phone number as the user entered it.</dd>			  \n" +
    "						</dl>\n" +
    "					</td>\n" +
    "				</tr>\n" +
    "				<tr id=\"description\">\n" +
    "					<td>\n" +
    "						description\n" +
    "					</td>\n" +
    "				<td>\n" +
    "					Contains the error message if isSuccessful is \"false\".\n" +
    "				</td>\n" +
    "				</tr>\n" +
    "				\n" +
    "				<tr id=\"requestMethod\">\n" +
    "					<td>\n" +
    "						requestMethod\n" +
    "					</td>\n" +
    "				<td>\n" +
    "					Request method. Default to \"GET\", \"POST\" or \"DELETE\" depending on the HTTP method.\n" +
    "				</td>\n" +
    "				</tr>\n" +
    "				\n" +
    "				<tr id=\"isSuccessful\">\n" +
    "					<td>\n" +
    "						isSuccessful\n" +
    "					</td>\n" +
    "				<td>\n" +
    "					true if operation is successful. Otherwise returns false.\n" +
    "				</td>\n" +
    "				</tr>\n" +
    "				\n" +
    "				\n" +
    "			</tbody>\n" +
    "			</table>\n" +
    "\n" +
    "			\n" +
    "		</div>\n" +
    "	</div>\n" +
    "</div>");
}]);

angular.module("camera/camera.tpl.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("camera/camera.tpl.html",
    "<div class=\"container-fluid\">\n" +
    "	<div class=\"row\">\n" +
    "		<div class=\"col-md-3\" id=\"sidebar\" ng-include=\"'assets/view/services_sidebar.html'\"></div>\n" +
    "		\n" +
    "		\n" +
    "		<div class=\"col-md-9\" id=\"content\">\n" +
    "			\n" +
    "			<div class=\"alert alert-info\">\n" +
    "				<h3 class=\"alert-heading\">Camera API</h3>\n" +
    "			</div>\n" +
    "						\n" +
    "			<br/>\n" +
    "			\n" +
    "			<div class=\"alert alert-success\">				\n" +
    "				<h4 class=\"alert-heading\"> <button class=\"btn btn-default btn-xs\">Get</button> Capture Photo</h4>\n" +
    "				<br/>\n" +
    "				<p class=\"long-wrap\">\n" +
    "				<a href=\"{{cameraLink}}\">{{cameraLink}}</a>?flash=<b>{0|1}</b>&front=<b>{0|1}</b>&resolution=<b>{0|1|2|3}</b>&focus=<b>{0|1|2|3|4|5|6}</b>\n" +
    "				</p>\n" +
    "			</div>\n" +
    "			\n" +
    "			<table id=\"sms_api_doc\" class=\"table table-striped\">\n" +
    "				<thead>\n" +
    "					<tr>\n" +
    "						<th>Parameter</th>\n" +
    "						<th>Description</th>\n" +
    "					</tr>\n" +
    "				</thead>\n" +
    "				\n" +
    "				<tbody>    	      \n" +
    "					<tr id=\"Flash\">\n" +
    "						<td>\n" +
    "							flash\n" +
    "						</td>\n" +
    "						<td>\n" +
    "							Optional parameter. 0 for no flash, any other values indicate flash is required. Default to 0.\n" +
    "						</td>\n" +
    "					</tr>\n" +
    "					\n" +
    "					<tr id=\"Front\">\n" +
    "						<td>\n" +
    "							front\n" +
    "						</td>\n" +
    "						<td>\n" +
    "							Optional parameter. 0 for rear facing camera, any other values indicate front facing camera. Default to 0.\n" +
    "						</td>\n" +
    "					</tr>\n" +
    "\n" +
    "					<tr id=\"Resolution\">\n" +
    "						<td>\n" +
    "							resolution\n" +
    "						</td>\n" +
    "						<td>\n" +
    "							Optional parameter. Resolution to be used to capture the photo. Possible values are \n" +
    "\n" +
    "							<dl class=\"dl-horizontal\">\n" +
    "							  <dt>0</dt>\n" +
    "							  <dd>Default resolution.</dd>\n" +
    "							  <dt>1</dt>\n" +
    "							  <dd>Low resolution.</dd>\n" +
    "							  <dt>2</dt>\n" +
    "							  <dd>Medium resolution.</dd>\n" +
    "							  <dt>3</dt>\n" +
    "							  <dd>High resolution.</dd>							  \n" +
    "							</dl>\n" +
    "						</td>\n" +
    "					</tr>\n" +
    "\n" +
    "					<tr id=\"Focus\">\n" +
    "						<td>\n" +
    "							focus\n" +
    "						</td>\n" +
    "						<td>\n" +
    "							Optional parameter. Focus mode to be used to capture the photo. Possible values are\n" +
    "\n" +
    "							<dl class=\"dl-horizontal\">\n" +
    "							  <dt>0</dt>\n" +
    "							  <dd>Auto-focus mode.</dd>\n" +
    "							  <dt>1</dt>\n" +
    "							  <dd>Continuous auto focus mode intended for taking pictures.</dd>\n" +
    "							  <dt>2</dt>\n" +
    "							  <dd>Continuous auto focus mode intended for video recording.</dd>\n" +
    "							  <dt>3</dt>\n" +
    "							  <dd>Extended depth of field (EDOF). Focusing is done digitally and continuously.</dd>	\n" +
    "							  <dt>4</dt>\n" +
    "							  <dd>Focus is fixed.</dd>	\n" +
    "							  <dt>5</dt>\n" +
    "							  <dd>Focus is set at infinity.</dd>	\n" +
    "							  <dt>6</dt>\n" +
    "							  <dd>Macro (close-up) focus mode.</dd>							  \n" +
    "							</dl>\n" +
    "						</td>\n" +
    "					</tr>					\n" +
    "				</tbody>\n" +
    "			</table>\n" +
    "			\n" +
    "			\n" +
    "			<div class=\"alert alert-warning\">\n" +
    "				<h4 class=\"alert-heading\">Response</h4>\n" +
    "				<p class=\"long-wrap\">\n" +
    "					Photo in JPEG format.\n" +
    "				</p>\n" +
    "			</div>		\n" +
    "			\n" +
    "			\n" +
    "		</div>\n" +
    "	</div>\n" +
    "</div>");
}]);

angular.module("contact/contact.tpl.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("contact/contact.tpl.html",
    "<div class=\"container-fluid\">\n" +
    "	<div class=\"row\">\n" +
    "		<div class=\"col-md-3\" id=\"sidebar\"\n" +
    "			ng-include=\"'assets/view/services_sidebar.html'\"></div>\n" +
    "		<div class=\"col-md-9\" id=\"content\">\n" +
    "\n" +
    "			<div class=\"alert alert-info\">\n" +
    "				<h3 class=\"alert-heading\">Contact API</h3>\n" +
    "			</div>\n" +
    "\n" +
    "			<div class=\"alert alert-success\">\n" +
    "				<h4 class=\"alert-heading\">\n" +
    "					<button class=\"btn btn-default btn-xs\">GET</button>\n" +
    "					List Contact\n" +
    "				</h4>\n" +
    "				<p></p>\n" +
    "				<br/> \n" +
    "				<p class=\"long-wrap\">\n" +
    "				<a ng-href=\"{{contactLink}}\">{{contactLink}}</a>?name=<b>{name}</b>&number=<b>{number}</b>\n" +
    "				</p>\n" +
    "			</div>\n" +
    "\n" +
    "			<table id=\"sms_api_doc\" class=\"table table-striped\">\n" +
    "				<thead>\n" +
    "					<tr>\n" +
    "						<th>Parameter</th>\n" +
    "						<th>Description</th>\n" +
    "					</tr>\n" +
    "				</thead>\n" +
    "\n" +
    "				<tbody>\n" +
    "					<tr id=\"name\">\n" +
    "						<td nowrap>name</td>\n" +
    "						<td>Contact name.</td>\n" +
    "					</tr>\n" +
    "\n" +
    "					<tr id=\"number\">\n" +
    "						<td nowrap>number</td>\n" +
    "						<td>Phone number.</td>\n" +
    "					</tr>\n" +
    "				</tbody>\n" +
    "			</table>\n" +
    "\n" +
    "\n" +
    "			<div class=\"alert alert-warning\">\n" +
    "				<h4 class=\"alert-heading\">Response</h4>\n" +
    "\n" +
    "				<p class=\"long-wrap\">\n" +
    "\n" +
    "				<code>\n" +
    "					{\"contacts\":[{\"emails\":[],\"groups\":[{\"id\":\"7\",\"name\":\"My\n" +
    "					Contacts\"}], </code>\n" +
    "				<br />\n" +
    "				<code>\n" +
    "					\"name\":\"Contact1\",\"vCard\":\"BEGIN:VCARD\\r\\nVERSION:2.1\\r\\nN:;Contact1;;;\n" +
    "				</code>\n" +
    "				<br />\n" +
    "				<code> \\r\\nFN:John Smith\\r\\nTEL;CELL:0123456789\\r\\nEND:VCARD\\r\\n\",\n" +
    "				</code>\n" +
    "				<br />\n" +
    "				<code>\n" +
    "					\"phones\":[{\"displayLabel\":\"Mobile\",\"number\":\"0123456789\",\"type\":2}],\"personId\":1116}],\n" +
    "				</code>\n" +
    "				<br />\n" +
    "				<code>\"description\":\"\",\"requestMethod\":\"GET\",\"isSuccessful\":true} </code>\n" +
    "\n" +
    "				</p>\n" +
    "			</div>\n" +
    "\n" +
    "\n" +
    "			<table id=\"sms_api_doc\" class=\"table table-striped\">\n" +
    "				<thead>\n" +
    "					<tr>\n" +
    "						<th>Field</th>\n" +
    "						<th>Description</th>\n" +
    "					</tr>\n" +
    "				</thead>\n" +
    "\n" +
    "				<tbody>\n" +
    "					<tr id=\"contacts\">\n" +
    "						<td>contacts</td>\n" +
    "						<td>A list of contact objects with the following fields <br />\n" +
    "						<br />\n" +
    "\n" +
    "							<dl class=\"dl-horizontal\">\n" +
    "								<dt>emails</dt>\n" +
    "								<dd>List of emails.</dd>\n" +
    "								<dt>groups</dt>\n" +
    "								<dd>List of groups.</dd>\n" +
    "								<dt>name</dt>\n" +
    "								<dd>Contact name.</dd>\n" +
    "								<dt>vCard</dt>\n" +
    "								<dd>Contact vCard</dd>\n" +
    "								<dt>phones</dt>\n" +
    "								<dd>List of phones.</dd>\n" +
    "								<dt>personId</dt>\n" +
    "								<dd>Unique identifier.</dd>\n" +
    "								<dt>ringTone</dt>\n" +
    "								<dd>Ringtone content URI.</dd>\n" +
    "							</dl>\n" +
    "\n" +
    "						</td>\n" +
    "					</tr>\n" +
    "\n" +
    "\n" +
    "					<tr id=\"description\">\n" +
    "						<td>description</td>\n" +
    "						<td>Contains the error message if isSuccessful is \"false\".</td>\n" +
    "					</tr>\n" +
    "\n" +
    "					<tr id=\"requestMethod\">\n" +
    "						<td>requestMethod</td>\n" +
    "						<td>Request method. Default to \"GET\", \"POST\" or \"DELETE\"\n" +
    "							depending on the HTTP method.</td>\n" +
    "					</tr>\n" +
    "\n" +
    "					<tr id=\"requestMethod\">\n" +
    "						<td>isSuccessful</td>\n" +
    "						<td>true if operation is successful. Otherwise returns false.\n" +
    "						</td>\n" +
    "					</tr>\n" +
    "\n" +
    "				</tbody>\n" +
    "			</table>\n" +
    "\n" +
    "\n" +
    "			<div class=\"alert alert-success\">\n" +
    "				<h4 class=\"alert-heading\">\n" +
    "					<button class=\"btn btn-default btn-xs\">POST</button>\n" +
    "					Add Contact\n" +
    "				</h4>\n" +
    "				<p></p>\n" +
    "				<br/> \n" +
    "				<p class=\"long-wrap\">\n" +
    "				{{contactLink}}\n" +
    "				</p>\n" +
    "			</div>\n" +
    "\n" +
    "			<table id=\"sms_api_doc\" class=\"table table-striped\">\n" +
    "				<thead>\n" +
    "					<tr>\n" +
    "						<th>Parameter</th>\n" +
    "						<th>Description</th>\n" +
    "					</tr>\n" +
    "				</thead>\n" +
    "\n" +
    "				<tbody>\n" +
    "					<tr id=\"name\">\n" +
    "						<td nowrap>name</td>\n" +
    "						<td>Display name.</td>\n" +
    "					</tr>\n" +
    "\n" +
    "					<tr>\n" +
    "						<td nowrap>mobile</td>\n" +
    "						<td>Mobile phone number.</td>\n" +
    "					</tr>\n" +
    "\n" +
    "					<tr>\n" +
    "						<td nowrap>home</td>\n" +
    "						<td>Home phone number.</td>\n" +
    "					</tr>\n" +
    "\n" +
    "					<tr>\n" +
    "						<td nowrap>work</td>\n" +
    "						<td>Work phone number.</td>\n" +
    "					</tr>\n" +
    "\n" +
    "					<tr>\n" +
    "						<td nowrap>email</td>\n" +
    "						<td>Email.</td>\n" +
    "					</tr>\n" +
    "\n" +
    "					<tr>\n" +
    "						<td nowrap>company</td>\n" +
    "						<td>Company name.</td>\n" +
    "					</tr>\n" +
    "\n" +
    "					<tr>\n" +
    "						<td nowrap>jobtitle</td>\n" +
    "						<td>Job title.</td>\n" +
    "					</tr>\n" +
    "				</tbody>\n" +
    "			</table>\n" +
    "\n" +
    "\n" +
    "			<div class=\"alert alert-warning\">\n" +
    "				<h4 class=\"alert-heading\">Response</h4>\n" +
    "\n" +
    "				<p class=\"long-wrap\">\n" +
    "				<code>\n" +
    "				{\n" +
    "				    \"contact\": {\n" +
    "				        \"company\": \"\",\n" +
    "				        \"displayName\": \"AAAAA\",\n" +
    "				</code>\n" +
    "				<br />\n" +
    "				<code>\n" +
    "				        \"email\": \"\",\n" +
    "				        \"homeNumber\": \"\",\n" +
    "				        \"jobTitle\": \"\",\n" +
    "				</code>\n" +
    "				<br />\n" +
    "				<code>\n" +
    "				        \"mobileNumber\": \"1212121\",\n" +
    "				        \"workNumber\": \"\"\n" +
    "				    },\n" +
    "				</code>\n" +
    "				<br />\n" +
    "				<code>\n" +
    "				    \"description\": \"\",\n" +
    "				    \"requestMethod\": \"POST\",\n" +
    "				    \"isSuccessful\": true\n" +
    "				}\n" +
    "				</code>\n" +
    "				</p>\n" +
    "			</div>\n" +
    "\n" +
    "\n" +
    "			<table id=\"sms_api_doc\" class=\"table table-striped\">\n" +
    "				<thead>\n" +
    "					<tr>\n" +
    "						<th>Field</th>\n" +
    "						<th>Description</th>\n" +
    "					</tr>\n" +
    "				</thead>\n" +
    "\n" +
    "				<tbody>\n" +
    "					<tr id=\"contacts\">\n" +
    "						<td>contacts</td>\n" +
    "						<td>Contact object with the following fields <br />\n" +
    "						<br />\n" +
    "\n" +
    "							<dl class=\"dl-horizontal\">\n" +
    "								<dt>company</dt>\n" +
    "								<dd>Company name.</dd>\n" +
    "								<dt>displayName</dt>\n" +
    "								<dd>Display name.dd>\n" +
    "								<dt>email</dt>\n" +
    "								<dd>Email.</dd>\n" +
    "								<dt>homeNumber</dt>\n" +
    "								<dd>Home phone number.</dd>\n" +
    "								<dt>jobTitle</dt>\n" +
    "								<dd>Job title.</dd>\n" +
    "								<dt>mobileNumber</dt>\n" +
    "								<dd>Mobile phone number.</dd>\n" +
    "								<dt>workNumber</dt>\n" +
    "								<dd>Work phone number.</dd>\n" +
    "							</dl>\n" +
    "\n" +
    "						</td>\n" +
    "					</tr>\n" +
    "\n" +
    "\n" +
    "					<tr id=\"description\">\n" +
    "						<td>description</td>\n" +
    "						<td>Contains the error message if isSuccessful is \"false\".</td>\n" +
    "					</tr>\n" +
    "\n" +
    "					<tr id=\"requestMethod\">\n" +
    "						<td>requestMethod</td>\n" +
    "						<td>Request method. Default to \"GET\", \"POST\" or \"DELETE\"\n" +
    "							depending on the HTTP method.</td>\n" +
    "					</tr>\n" +
    "\n" +
    "					<tr id=\"requestMethod\">\n" +
    "						<td>isSuccessful</td>\n" +
    "						<td>true if operation is successful. Otherwise returns false.\n" +
    "						</td>\n" +
    "					</tr>\n" +
    "\n" +
    "				</tbody>\n" +
    "			</table>\n" +
    "\n" +
    "\n" +
    "\n" +
    "			<div class=\"alert alert-success\">\n" +
    "				<h4 class=\"alert-heading\">\n" +
    "					<button class=\"btn btn-default btn-xs\">DELETE</button>\n" +
    "					Delete Contact\n" +
    "				</h4>\n" +
    "				<p></p>\n" +
    "				<br/> \n" +
    "				<p class=\"long-wrap\">\n" +
    "				{{contactLink}}<b>{id}</b>\n" +
    "				</p>\n" +
    "			</div>\n" +
    "\n" +
    "			<table id=\"sms_api_doc\" class=\"table table-striped\">\n" +
    "				<thead>\n" +
    "					<tr>\n" +
    "						<th>Parameter</th>\n" +
    "						<th>Description</th>\n" +
    "					</tr>\n" +
    "				</thead>\n" +
    "\n" +
    "				<tbody>\n" +
    "					<tr>\n" +
    "						<td nowrap>id</td>\n" +
    "						<td>Person id which is a unique identifier for the contact.</td>\n" +
    "					</tr>				\n" +
    "				</tbody>\n" +
    "			</table>\n" +
    "\n" +
    "\n" +
    "			<div class=\"alert alert-warning\">\n" +
    "				<h4 class=\"alert-heading\">Response</h4>\n" +
    "\n" +
    "				<p class=\"long-wrap\">\n" +
    "				<code>\n" +
    "				{\n" +
    "			    \"count\": 1,\n" +
    "			    \"description\": \"Successfully deleted the contact with id 35976\",\n" +
    "			    </code>\n" +
    "				<br />\n" +
    "				<code>\n" +
    "			    \"requestMethod\": \"DELETE\",\n" +
    "			    \"isSuccessful\": true\n" +
    "				}\n" +
    "				</code>\n" +
    "				</p>\n" +
    "			</div>\n" +
    "\n" +
    "\n" +
    "			<table id=\"sms_api_doc\" class=\"table table-striped\">\n" +
    "				<thead>\n" +
    "					<tr>\n" +
    "						<th>Field</th>\n" +
    "						<th>Description</th>\n" +
    "					</tr>\n" +
    "				</thead>\n" +
    "\n" +
    "				<tbody>\n" +
    "					<tr>\n" +
    "						<td>count</td>\n" +
    "						<td>Number of deleted contact. Should be 1 if the deletion is successful.</td>\n" +
    "					</tr>\n" +
    "\n" +
    "					<tr id=\"description\">\n" +
    "						<td>description</td>\n" +
    "						<td>Contains the error message if isSuccessful is \"false\".</td>\n" +
    "					</tr>\n" +
    "\n" +
    "					<tr id=\"requestMethod\">\n" +
    "						<td>requestMethod</td>\n" +
    "						<td>Request method. Default to \"GET\", \"POST\" or \"DELETE\"\n" +
    "							depending on the HTTP method.</td>\n" +
    "					</tr>\n" +
    "\n" +
    "					<tr id=\"requestMethod\">\n" +
    "						<td>isSuccessful</td>\n" +
    "						<td>true if operation is successful. Otherwise returns false.\n" +
    "						</td>\n" +
    "					</tr>\n" +
    "\n" +
    "				</tbody>\n" +
    "			</table>\n" +
    "\n" +
    "\n" +
    "\n" +
    "\n" +
    "\n" +
    "			<div class=\"alert alert-info\">\n" +
    "				<p>\n" +
    "				<h4 id=\"contactusage\" class=\"alert-heading\">Usage </h3>\n" +
    "				<a name=\"contactusage\">\n" +
    "				</p> <br/>				\n" +
    "				 <div class=\"gridStyle\" ng-grid=\"contactGrid\">\n" +
    "				</div>	\n" +
    "			</div>\n" +
    "			\n" +
    "\n" +
    "		</div>\n" +
    "	</div>\n" +
    "\n" +
    "\n" +
    "</div>\n" +
    "\n" +
    "\n" +
    "\n" +
    "\n" +
    "\n" +
    "");
}]);

angular.module("controlpanel/controlpanel.tpl.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("controlpanel/controlpanel.tpl.html",
    "<div class=\"container-fluid\">\n" +
    "\n" +
    "    <div class=\"row-fluid\">\n" +
    "       \n" +
    "            <div class=\"panel panel-default\">\n" +
    "                <div class=\"panel-heading\">\n" +
    "                    <h3 class=\"panel-title\">\n" +
    "                        Control Panel &nbsp;&nbsp;&nbsp;<span class=\"label label-primary\">{{deviceInfo.deviceName}}</span> </h3>  \n" +
    "                </div>\n" +
    "				\n" +
    "               	 <div ng-if=\"loadingInProgress\">\n" +
    "                   <br/>\n" +
    "                   &nbsp;&nbsp;&nbsp; <img src=\"assets/images/record.gif\">&nbsp;&nbsp;&nbsp; <span class=\"label label-default\">\n" +
    "                   Loading...please wait\n" +
    "                   </span>  \n" +
    "                 </div>\n" +
    "                \n" +
    "                <toaster-container toaster-options=\"{'position-class': 'toast-center'}\"></toaster-container>\n" +
    "                \n" +
    "                <div class=\"panel-body\">\n" +
    "                    <div class=\"row-fluid\">\n" +
    "                    \n" +
    "                    \n" +
    "                     <div class=\"panel panel-info\">\n" +
    "		                <div class=\"panel-heading\">\n" +
    "		                    <h3 class=\"panel-title\">\n" +
    "		                        <span class=\"glyphicon glyphicon-phone\"></span> Information</span></h3>\n" +
    "		                </div>\n" +
    "		                \n" +
    "		                <div class=\"panel-body\">\n" +
    "		                    <div class=\"row-fluid\">\n" +
    "		                    \n" +
    "		                      <a ng-click=\"showDeviceInfo()\" class=\"btn btn-info\" role=\"button\" uib-tooltip-placement=\"top\" uib-tooltip=\"General device information\"><span class=\"glyphicon glyphicon-info-sign\"></span> <br/>Device Info</a>\n" +
    "                        	  <a ng-click=\"showViewer()\" class=\"btn btn-info\" role=\"button\" uib-tooltip-placement=\"top\" uib-tooltip=\"Photos in Google Drive\"><span class=\"glyphicon glyphicon-picture\"></span> <br/>Photos</a>\n" +
    "                        \n" +
    "                        \n" +
    "		                    </div>\n" +
    "		                </div>                    \n" +
    "                    </div>\n" +
    "                    \n" +
    "                    \n" +
    "                    <div class=\"panel panel-info\">\n" +
    "		                <div class=\"panel-heading\">\n" +
    "		                    <h3 class=\"panel-title\">\n" +
    "		                        <span class=\"glyphicon glyphicon-phone\"></span> Status</span>	                        \n" +
    "								<span ng-class=\"stateToClass()\">{{serviceStatus}}</span>\n" +
    "		                    </h3>\n" +
    "		                </div>\n" +
    "		                \n" +
    "		                <div class=\"panel-body\">\n" +
    "		                    <div class=\"row-fluid\">\n" +
    "\n" +
    "		                        <div class=\"btn-group\">        \n" +
    "		                         <button type=\"button\"  class=\"btn btn-primary dropdown-toggle\" data-toggle=\"dropdown\" aria-haspopup=\"true\" aria-expanded=\"false\">\n" +
    "								    <span class=\"glyphicon glyphicon-th-large\"></span> <br/>Service <span class=\"caret\"></span>\n" +
    "								  </button>\n" +
    "								  \n" +
    "								  <ul class=\"dropdown-menu\">\n" +
    "								   <li><a ng-click=\"postAction('3','0');\" uib-tooltip-placement=\"top\" uib-tooltip=\"Command may be delayed depending on your network. It is better to have the service stayed started.\"><span class=\"glyphicon glyphicon-play\"></span>&nbsp;Start&nbsp;</a></li>\n" +
    "								   <li><a ng-click=\"postAction('3','1');\" uib-tooltip-placement=\"top\" uib-tooltip=\"Stop the service\"><span class=\"glyphicon glyphicon-stop\"></span>&nbsp;Stop&nbsp;</a></li>\n" +
    "								  </ul>\n" +
    "								  \n" +
    "								</div>\n" +
    "                        \n" +
    "		                    </div>\n" +
    "		                </div>                    \n" +
    "                    </div>                  \n" +
    "                        \n" +
    "                    \n" +
    "                     <!--\n" +
    "                     <div class=\"panel panel-info\">\n" +
    "		                <div class=\"panel-heading\">\n" +
    "		                    <h3 class=\"panel-title\">\n" +
    "		                        <span class=\"glyphicon glyphicon-phone\"></span> Surveillance\n" +
    "		                        \n" +
    "		                        <span class=\"label label-success\">{{commandStatus}}</span>\n" +
    "		                        \n" +
    "		                        </h3>\n" +
    "		                </div>\n" +
    "		                \n" +
    "		                <div class=\"panel-body\">\n" +
    "		                    <div class=\"row-fluid\">\n" +
    "				                    \n" +
    "				                 <div class=\"btn-group\">\n" +
    "								  \n" +
    "								  <button type=\"button\" class=\"btn btn-primary dropdown-toggle\" data-toggle=\"dropdown\" aria-haspopup=\"true\" aria-expanded=\"false\">\n" +
    "								    <span class=\"glyphicon glyphicon-th-large\"></span> <br/>Surveillance <span class=\"caret\"></span>\n" +
    "								  </button>\n" +
    "								  <ul class=\"dropdown-menu\">\n" +
    "								  	<li><a uib-tooltip-placement=\"top\" uib-tooltip=\"Start surveillance\" ng-click=\"triggerSurveillance('surveillance','start');\"><span class=\"glyphicon glyphicon-play\"></span>&nbsp;Start&nbsp;</a></li>\n" +
    "								   	<li><a uib-tooltip-placement=\"top\" uib-tooltip=\"Stop surveillance\" ng-click=\"triggerSurveillance('surveillance','stop');\"><span class=\"glyphicon glyphicon-stop\"></span>&nbsp;Stop&nbsp;</a></li>\n" +
    "								  </ul> \n" +
    "								  \n" +
    "								</div>\n" +
    "								\n" +
    "								<div class=\"btn-group\">\n" +
    "								  \n" +
    "								  <button type=\"button\" class=\"btn btn-primary dropdown-toggle\" data-toggle=\"dropdown\" aria-haspopup=\"true\" aria-expanded=\"false\">\n" +
    "								    <span class=\"glyphicon glyphicon-th-large\"></span> <br/>Switch Camera <span class=\"caret\"></span>\n" +
    "								  </button>\n" +
    "								  <ul class=\"dropdown-menu\">\n" +
    "								   <li><a uib-tooltip-placement=\"top\" uib-tooltip=\"Switch to front camera\" ng-click=\"switchCamera('camera','front');\"><span class=\"glyphicon glyphicon-chevron-left\"></span>&nbsp;Front Camera&nbsp;</a></li>\n" +
    "								   <li><a uib-tooltip-placement=\"top\" uib-tooltip=\"Switch to rear camera\" ng-click=\"switchCamera('camera','back');\"><span class=\"glyphicon glyphicon-chevron-right\"></span>&nbsp;Rear Camera&nbsp;</a></li>\n" +
    "								  </ul> \n" +
    "								  \n" +
    "								</div> \n" +
    "								 \n" +
    "								<div class=\"btn-group\">\n" +
    "								  \n" +
    "								  <button type=\"button\" class=\"btn btn-primary dropdown-toggle\" data-toggle=\"dropdown\" aria-haspopup=\"true\" aria-expanded=\"false\">\n" +
    "								    <span class=\"glyphicon glyphicon-th-large\"></span> <br/>Ring <span class=\"caret\"></span>\n" +
    "								  </button>\n" +
    "								  <ul class=\"dropdown-menu\">\n" +
    "								  	<li><a uib-tooltip-placement=\"top\" uib-tooltip=\"Play ringing sound\" ng-click=\"postGTalkAction('ring','100');\"><span class=\"glyphicon glyphicon-play\"></span>&nbsp;Start&nbsp;</a></li>\n" +
    "								   	<li><a uib-tooltip-placement=\"top\" uib-tooltip=\"Stop ringing sound\" ng-click=\"postGTalkAction('ring','stop');\"><span class=\"glyphicon glyphicon-stop\"></span>&nbsp;Stop&nbsp;</a></li>\n" +
    "								  </ul> 						  						  \n" +
    "								</div>                        \n" +
    "		                 </div>\n" +
    "\n" +
    "		               </div>                    \n" +
    "		             </div> \n" +
    "                	-->\n" +
    "                \n" +
    "                    <!-- \n" +
    "                     <div class=\"panel panel-info\">\n" +
    "		                <div class=\"panel-heading\">\n" +
    "		                    <h3 class=\"panel-title\">\n" +
    "		                        <span class=\"glyphicon glyphicon-phone\"></span> Spy Camera</span>\n" +
    "		                        </h3>\n" +
    "		                </div>\n" +
    "		                \n" +
    "		                <div class=\"panel-body\">\n" +
    "		                 	\n" +
    "			                <h4 class=\"panel-title\">\n" +
    "					            <a data-toggle=\"collapse\" id=\"spy_settings\" href ng-click=\"selectSpySettings()\">\n" +
    "					              Settings <b class=\"caret\"></b>\n" +
    "					            </a>\n" +
    "					         </h4>\n" +
    "                       	\n" +
    "                     		  		\n" +
    "		                 	<div class=\"row-fluid\" ng-show=\"spySettings\">		\n" +
    "		                 			<br/>                 		                		\n" +
    "				                 	<form class=\"form-horizontal\" role=\"form\">\n" +
    "									  \n" +
    "									  <div class=\"form-group\">\n" +
    "									  	<label for=\"spy_camera_id\" class=\"col-sm-4 control-label\">Camera</label>\n" +
    "									  	<div class=\"col-sm-3\">		 \n" +
    "										  	<select class=\"form-control\" id=\"spy_camera_id\" ng-model=\"spyCamera.id\">\n" +
    "													  <option value=\"front\">Front</option>\n" +
    "													  <option value=\"rear\">Rear</option>\n" +
    "											</select>\n" +
    "										</div>										\n" +
    "									  </div>\n" +
    "									  \n" +
    "									   <div class=\"form-group\">\n" +
    "									  	<label for=\"spy_camera_interval\" class=\"col-sm-4 control-label\">Every</label>\n" +
    "									  	<div class=\"col-sm-3\">		 \n" +
    "										  	<input type=\"number\" class=\"form-control\" ng-model=\"spyCamera.interval\" id=\"spy_camera_interval\" ng-minlength=\"1\" ng-maxlength=\"10000\" min=\"1\" max=\"10000\" placeholder=\"Interval\"/>\n" +
    "										</div>										\n" +
    "									  </div>\n" +
    "									  \n" +
    "									  <div class=\"form-group\">\n" +
    "									  	<label for=\"spy_camera_uom\" class=\"col-sm-4 control-label\">&nbsp;</label>\n" +
    "									  	<div class=\"col-sm-3\">		 \n" +
    "										  	<select class=\"form-control\" id=\"spy_camera_uom\" ng-model=\"spyCamera.uom\">\n" +
    "													  <option value=\"minutes\">Minutes</option>\n" +
    "													  <option value=\"seconds\">Seconds</option>\n" +
    "											</select>\n" +
    "										</div>										\n" +
    "									  </div>\n" +
    "									  \n" +
    "									</form>									\n" +
    "		                 	</div>	\n" +
    "		                 	\n" +
    "		                 	 <div class=\"row-fluid\">		                    \n" +
    "		                    	  <br/>\n" +
    "				                 <div class=\"btn-group\">\n" +
    "								  \n" +
    "								  <button type=\"button\" class=\"btn btn-primary dropdown-toggle\" data-toggle=\"dropdown\" aria-haspopup=\"true\" aria-expanded=\"false\">\n" +
    "								    <span class=\"glyphicon glyphicon-th-large\"></span> <br/>Spy<span class=\"caret\"></span>\n" +
    "								  </button>\n" +
    "								  <ul class=\"dropdown-menu\">\n" +
    "								  	<li><a uib-tooltip-placement=\"top\" uib-tooltip=\"Turn into a spy camera to take photo at configured interval\" ng-click=\"triggerSpyCamera('spy','start');\"><span class=\"glyphicon glyphicon-play\"></span>&nbsp;Start&nbsp;</a></li>\n" +
    "								   	<li><a ng-click=\"triggerSpyCamera('spy','stop');\"><span class=\"glyphicon glyphicon-stop\"></span>&nbsp;Stop&nbsp;</a></li>\n" +
    "								  </ul> \n" +
    "								  \n" +
    "								</div>    \n" +
    "		                 	</div>\n" +
    "		                 		\n" +
    "		                 	\n" +
    "		                 	                 	\n" +
    "		               </div>                    \n" +
    "		             </div> \n" +
    "		             \n" +
    "		             -->\n" +
    "		             \n" +
    "		             			 \n" +
    "                    </div>                    \n" +
    "                </div>\n" +
    "            </div>\n" +
    "    </div>\n" +
    "</div>");
}]);

angular.module("device/device.tpl.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("device/device.tpl.html",
    "<div class=\"container-fluid\">\n" +
    "\n" +
    "    <div class=\"row\">\n" +
    "    \n" +
    "	   <div ng-if=\"loadingInProgress\"> \n" +
    "             <br/>\n" +
    "             &nbsp;&nbsp;&nbsp; <img src=\"assets/images/record.gif\">&nbsp;&nbsp;&nbsp; <span class=\"label label-default\">\n" +
    "             Loading...please wait\n" +
    "             </span>  \n" +
    "        </div>\n" +
    "                 \n" +
    "        <ul class=\"thumbnails\">        \n" +
    "            <div class=\"col-md-4\" ng-repeat=\"device in devices\">\n" +
    "                <div class=\"thumbnail\">                	\n" +
    "                	<div class=\"caption text-center\">\n" +
    "                         <h3>{{device.deviceName}}</h3>\n" +
    "                         <span class=\"label label-info\">{{device.deviceId}}</span> <span class=\"label label-info\">{{device.email}}</span>\n" +
    "                         <br/><br/>\n" +
    "                         <div ng-switch on=\"device.isServiceStarted\">\n" +
    "					            <span ng-switch-when=\"true\">\n" +
    "					            	<a class=\"btn  btn-sm btn-success\" role=\"button\" href='{{device.uri}}'>{{device.uri}}</a>\n" +
    "					            </span>\n" +
    "					            <span class=\"label label-warning\" ng-switch-default>Service not available</span>\n" +
    "					      </div>\n" +
    "                    </div>\n" +
    "					\n" +
    "					<div class=\"caption text-center\">\n" +
    "                        <p class=\"text-center\">\n" +
    "                        <a href=\"#controlpanel?name={{device.deviceName}}&id={{device.deviceId}}\" class=\"btn btn-primary\">Control Panel</a>\n" +
    "                        </p>\n" +
    "                   </div>   \n" +
    "                   \n" +
    "				  <div>\n" +
    "				    <uib-carousel interval=\"slideInterval\" no-wrap=\"noWrapSlides\" active=\"slideActive\">\n" +
    "				      <uib-slide ng-repeat=\"slide in device.slides\" index=\"slide.id\">\n" +
    "				        <img ng-src=\"{{slide.image}}\" style=\"margin:auto;\" class=\"img-responsive\">\n" +
    "				        <div class=\"carousel-caption\">\n" +
    "				          <p>{{slide.text}}</p>\n" +
    "				        </div>\n" +
    "				      </uib-slide>\n" +
    "				    </uib-carousel>\n" +
    "				  </div>                \n" +
    "                                     \n" +
    "                </div>\n" +
    "            </div>\n" +
    "        </ul>\n" +
    "    </div>\n" +
    "</div>");
}]);

angular.module("fullscreen/fullscreen.tpl.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("fullscreen/fullscreen.tpl.html",
    "<style type=\"text/css\">	\n" +
    "	html, body {\n" +
    "	  background: url({{mjpegUrl}}}) no-repeat center center fixed; \n" +
    "	  -webkit-background-size: cover;\n" +
    "	  -moz-background-size: cover;\n" +
    "	  -o-background-size: cover;\n" +
    "	  background-size: cover;	  \n" +
    "	  min-height: 100%; 	\n" +
    "	}\n" +
    "</style>\n" +
    "");
}]);

angular.module("gateway/gateway.tpl.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("gateway/gateway.tpl.html",
    "<div class=\"row-fluid\">\n" +
    "  <h1 class=\"page-header\">\n" +
    "    Messaging\n" +
    "  </h1>\n" +
    "  <p>\n" +
    "   \n" +
    "  </p>\n" +
    "\n" +
    "</div>\n" +
    "\n" +
    "");
}]);

angular.module("home/home.tpl.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("home/home.tpl.html",
    "<div class=\"marketing\">\n" +
    "  <div class=\"row\">\n" +
    "    <div class=\"col-xs-12 col-sm-6 col-md-4\">\n" +
    "      <a href ui-sref=\"surveillance\"><h4><i class=\"fa fa-eye\"></i> Surveillance</h4></a>\n" +
    "      <p>\n" +
    "        Use your phone camera as a remote video surveillance tool.\n" +
    "      </p>\n" +
    "    </div>\n" +
    "	\n" +
    "	<div class=\"col-xs-12 col-sm-6 col-md-4\">\n" +
    "      <a href ui-sref=\"tracker\"><h4><i class=\"fa fa-cog\"></i> Tracker</h4></a>\n" +
    "      <p>\n" +
    "      Track connected devices and received notifications.\n" +
    "      </p>\n" +
    "    </div>    \n" +
    "\n" +
    "  </div>\n" +
    "  \n" +
    "  <div class=\"row\">\n" +
    "  \n" +
    "    <div class=\"col-xs-12 col-sm-6 col-md-4\">\n" +
    "      <a href ui-sref=\"services\"><h4><i class=\"fa fa-magic\"></i> Services</h4></a>\n" +
    "      <p>\n" +
    "      Access your phone through the provided APIs.\n" +
    "      </p>\n" +
    "    </div>\n" +
    "	\n" +
    "	<div class=\"col-xs-12 col-sm-6 col-md-4\">\n" +
    "      <a href ui-sref=\"about\"><h4><i class=\"fa fa-info\"></i> About</h4></a>\n" +
    "      <p>\n" +
    "       What is myMobKit?\n" +
    "      </p>\n" +
    "    </div>\n" +
    "  </div>\n" +
    "</div>\n" +
    "\n" +
    "");
}]);

angular.module("image/image.tpl.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("image/image.tpl.html",
    "<div class=\"container-fluid\">\n" +
    "	<div class=\"row\">\n" +
    "		\n" +
    "		<div class=\"col-md-9\" id=\"content\">\n" +
    "			<div class=\"alert\">\n" +
    "				<p>\n" +
    "				<h3 class=\"alert-heading\">Image Gallery</h3>\n" +
    "				<br/>\n" +
    "				<div class=\"btn-group\">\n" +
    "				  <button type=\"button\" class=\"btn btn-warning\" ng-click=\"deleteImages()\">Delete</button>\n" +
    "				</div>\n" +
    "				\n" +
    "				<br/><br/>\n" +
    "				<div class=\"radio row\">\n" +
    "					<label>\n" +
    "					 <input type=\"checkbox\" ng-model=\"selectedAll\" ng-click=\"checkAll()\">&nbsp;&nbsp;&nbsp;Select all\n" +
    "					</label>\n" +
    "				</div>\n" +
    "				<br/>\n" +
    "				</p> 				\n" +
    "				\n" +
    "			<div class=\"row-fluid row-margin-bottom\">                   \n" +
    "	            <div ng-repeat=\"image in images\">	            	\n" +
    "	            	<div class='row' ng-show=\"image.dateTaken_CHANGED\"></div>\n" +
    "	            	<h3 ng-show=\"image.dateTaken_CHANGED\">{{image.dateTaken_DISPLAYED}}</h3>\n" +
    "		            <div  class=\"col-md-4 col-xs-6 col-sm-4 no-padding lib-item\" data-category=\"view\">\n" +
    "		                <div class=\"lib-panel\">\n" +
    "		                    <div class=\"row\">		                    \n" +
    "		                        <div class=\"col-md-5 col-xs-8 col-sm-4 lib-image-show\">\n" +
    "		                        	 <button class='btn btn-default' ng-click=\"showImage($index)\" >\n" +
    "		                            	<img class=\"img-responsive\" uib-tooltip-placement=\"top\" uib-tooltip=\"{{image.displayName}}\" ng-src=\"{{streamImageLink}}?uri={{image.contentUri}}&&id={{image.id}}&kind=1\" />\n" +
    "		                             </button>                  \n" +
    "		                        </div>\n" +
    "		                        \n" +
    "		                        <div class=\"lib-desc\">\n" +
    "                               		&nbsp;<input type=\"checkbox\" ng-model=\"image.dateTaken_SELECTED\" data-toggle=\"tooltip\" data-placement=\"right\" title=\"{{image.displayName}}\" />                               		\n" +
    "                            	</div>     \n" +
    "                            		\n" +
    "		                    </div>\n" +
    "		                </div>\n" +
    "		            </div> 		            \n" +
    "            	</div>\n" +
    "        	</div>				\n" +
    "		</div>\n" +
    "	</div>\n" +
    "   </div>	\n" +
    "</div>\n" +
    "	\n" +
    "");
}]);

angular.module("location/location.tpl.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("location/location.tpl.html",
    "<div class=\"container-fluid\">\n" +
    "	<div class=\"row\">\n" +
    "\n" +
    "\n" +
    "		<div class=\"col-md-3\" id=\"sidebar\" ng-include=\"'assets/view/services_sidebar.html'\"></div>\n" +
    "		\n" +
    "		\n" +
    "		<div class=\"col-md-9\" id=\"content\">\n" +
    "			\n" +
    "			<div class=\"alert alert-info\">\n" +
    "				<h3 class=\"alert-heading\">Location API</h3>\n" +
    "			</div>\n" +
    "						\n" +
    "			<br/>\n" +
    "			\n" +
    "			<div class=\"alert alert-success\">				\n" +
    "				<h4 class=\"alert-heading\"> <button class=\"btn btn-default btn-xs\">Get</button> Get Location</h4>\n" +
    "				<br/>\n" +
    "				<p class=\"long-wrap\">\n" +
    "				<a href=\"{{locationLink}}\">{{locationLink}}</a>\n" +
    "				</p>\n" +
    "			</div>\n" +
    "			\n" +
    "			<div class=\"alert alert-warning\">\n" +
    "				<h4 class=\"alert-heading\">Response</h4>\n" +
    "				<p class=\"long-wrap\">\n" +
    "				<code>\n" +
    "					{\"latitude\":3.1264249,\"longitude\":101.6391089,\n" +
    "					</code><br/><code>\n" +
    "					\"description\":\"\",\"requestMethod\":\"GET\",\"isSuccessful\":true}				\n" +
    "				</code>\n" +
    "				</p>\n" +
    "			</div>\n" +
    "\n" +
    "\n" +
    "			<table class=\"table table-striped\">\n" +
    "			<thead>\n" +
    "				<tr>\n" +
    "					<th>Field</th>\n" +
    "					<th>Description</th>\n" +
    "				</tr>\n" +
    "			</thead>\n" +
    "			\n" +
    "			<tbody>    	    \n" +
    "\n" +
    "				<tr>\n" +
    "					<td>\n" +
    "						latitude\n" +
    "					</td>\n" +
    "				<td>\n" +
    "					Latitude\n" +
    "				</td>\n" +
    "				</tr>\n" +
    "\n" +
    "				<tr>\n" +
    "					<td>\n" +
    "						longitude\n" +
    "					</td>\n" +
    "				<td>\n" +
    "					Longitude\n" +
    "				</td>\n" +
    "				</tr>\n" +
    "				\n" +
    "				<tr id=\"description\">\n" +
    "					<td>\n" +
    "						description\n" +
    "					</td>\n" +
    "				<td>\n" +
    "					Contains the error message if isSuccessful is \"false\".\n" +
    "				</td>\n" +
    "				</tr>\n" +
    "\n" +
    "				<tr>\n" +
    "					<td>\n" +
    "						requestMethod\n" +
    "					</td>\n" +
    "				<td>\n" +
    "					Request method. Default to \"GET\", \"POST\" or \"DELETE\" depending on the HTTP method.\n" +
    "				</td>\n" +
    "				</tr>\n" +
    "				\n" +
    "				<tr>\n" +
    "					<td>\n" +
    "						isSuccessful\n" +
    "					</td>\n" +
    "				<td>\n" +
    "					true if operation is successful. Otherwise returns false.\n" +
    "				</td>\n" +
    "				</tr>\n" +
    "\n" +
    "				\n" +
    "			</tbody>\n" +
    "			</table>			\n" +
    "\n" +
    "\n" +
    "\n" +
    "\n" +
    "			<div class=\"alert alert-info row\">\n" +
    "				<p>\n" +
    "				<h4 class=\"alert-heading\">Usage</h4>\n" +
    "				</p> <br/>	\n" +
    "				\n" +
    " 				<div id=\"map\"></div>\n" +
    "			    <div id=\"class\" ng-repeat=\"marker in markers | orderBy : 'title'\">\n" +
    "			         <a href=\"#\" ng-click=\"openInfoWindow($event, marker)\">{{marker.title}}</a>\n" +
    "			    </div>\n" +
    "\n" +
    "			</div>\n" +
    "			\n" +
    "			\n" +
    "		</div>\n" +
    "	</div>\n" +
    "</div>");
}]);

angular.module("login/login.tpl.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("login/login.tpl.html",
    "<style type=\"text/css\">\n" +
    "      .container > .content {\n" +
    "        background-color: #fff;\n" +
    "        padding: 20px;\n" +
    "        margin: 0 -20px; \n" +
    "        -webkit-border-radius: 10px 10px 10px 10px;\n" +
    "           -moz-border-radius: 10px 10px 10px 10px;\n" +
    "                border-radius: 10px 10px 10px 10px;\n" +
    "        -webkit-box-shadow: 0 1px 2px rgba(0,0,0,.15);\n" +
    "           -moz-box-shadow: 0 1px 2px rgba(0,0,0,.15);\n" +
    "                box-shadow: 0 1px 2px rgba(0,0,0,.15);\n" +
    "      }\n" +
    "\n" +
    "	  .login-form {\n" +
    "		margin-left: 30%;\n" +
    "	  }\n" +
    "	\n" +
    "	  legend {\n" +
    "		margin-right: -50px;\n" +
    "		font-weight: bold;\n" +
    "	  	color: #404040;\n" +
    "	  }\n" +
    "</style>\n" +
    "<div class=\"row-fluid\">\n" +
    " <div class=\"container-fluid\">\n" +
    "    <div class=\"content\">\n" +
    "      <div class=\"row\">\n" +
    "        <div class=\"login-form\">\n" +
    "          <h2>Login</h2>\n" +
    "          <form action=\"\">\n" +
    "            <fieldset>\n" +
    "              <div class=\"clearfix\">\n" +
    "                <input type=\"text\" placeholder=\"Username\">\n" +
    "              </div>\n" +
    "              <div class=\"clearfix\">\n" +
    "                <input type=\"password\" placeholder=\"Password\">\n" +
    "              </div>\n" +
    "              <div class=\"clearfix\">\n" +
    "			    {{status}}\n" +
    "			  </div>\n" +
    "			<br/>\n" +
    "              <button class=\"btn primary\" type=\"submit\">Sign in</button>\n" +
    "            </fieldset>\n" +
    "          </form>\n" +
    "        </div>\n" +
    "      </div>\n" +
    "    </div>\n" +
    "  </div> \n" +
    "\n" +
    "</div>\n" +
    "\n" +
    "\n" +
    "");
}]);

angular.module("media/media.tpl.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("media/media.tpl.html",
    "<div class=\"container-fluid\">\n" +
    "	<div class=\"row\">\n" +
    "		<div class=\"col-md-3\" id=\"sidebar\" ng-include=\"'assets/view/services_sidebar.html'\"></div>		\n" +
    "		\n" +
    "		<div class=\"col-md-9\" id=\"content\">\n" +
    "		\n" +
    "			<div class=\"alert alert-info\">\n" +
    "				<h3 class=\"alert-heading\">Media API</h3>\n" +
    "			</div>\n" +
    "			\n" +
    "			<div class=\"alert alert-success\">\n" +
    "				<h4 class=\"alert-heading\"> <button class=\"btn btn-default btn-xs\">GET</button> List Media </h4> \n" +
    "				<p></p> <br>\n" +
    "				\n" +
    "				<button type=\"button\" class=\"btn btn-info btn-xs\">Image</button><p class=\"long-wrap\">&nbsp;&nbsp;<a ng-href=\"{{allMediaLink}}image\">{{allMediaLink}}image</a></p>\n" +
    "				<br/><br/>\n" +
    "				<button type=\"button\" class=\"btn btn-info btn-xs\">Video&nbsp;&nbsp;&nbsp;</button><p class=\"long-wrap\">&nbsp;&nbsp;<a ng-href=\"{{allMediaLink}}video\">{{allMediaLink}}video</a></p>\n" +
    "				<br/><br/>\n" +
    "				<button type=\"button\" class=\"btn btn-info btn-xs\">Audio&nbsp;&nbsp;&nbsp;</button><p class=\"long-wrap\">&nbsp;&nbsp;<a ng-href=\"{{allMediaLink}}audio\">{{allMediaLink}}audio</a></p>\n" +
    "			</div>\n" +
    "			\n" +
    "			<div class=\"alert alert-warning\">\n" +
    "				<h4 class=\"alert-heading\">Response</h4>\n" +
    "				<p class=\"long-wrap\">\n" +
    "				<code>\n" +
    "				{\"images\":[{\"bucketDisplayName\":\"WhatsApp Images\",\"bucketId\":\"440583809\",\n" +
    "				</code><br/><code>\n" +
    "				\"contentUri\":\"content://media/external/images/media/26423\",\n" +
    "				</code><br/><code>\n" +
    "				\"data\":\"/storage/sdcard0/WhatsApp/Media/WhatsApp Images/IMG-20140507-WA0000.jpg\",\n" +
    "				</code><br/><code>\n" +
    "				\"width\":\"600\",\"displayName\":\"IMG-20140507-WA0000.jpg\",\"height\":\"800\",\n" +
    "				</code><br/><code>\n" +
    "				\"mimeType\":\"image/jpeg\",\"latitude\":0.0,\"longitude\":0.0,\"isPrivate\":0,\n" +
    "				</code><br/><code>\n" +
    "				\"orientation\":0,\"id\":26423,\"size\":79418,\"dateTaken\":-733725496},\n" +
    "				</code><br/><code>\n" +
    "				{\"bucketDisplayName\":\"Camera\",\"bucketId\":\"1509922574\",\n" +
    "				</code><br/><code>\n" +
    "				\"contentUri\":\"content://media/external/images/media/26433\",\n" +
    "				</code><br/><code>\n" +
    "				\"data\":\"/storage/sdcard0/DCIM/Camera/20140510_104903.jpg\",\n" +
    "				</code><br/><code>\n" +
    "				\"width\":\"2560\",\"displayName\":\"20140510_104903.jpg\",\"height\":\"1920\",\n" +
    "				</code><br/><code>\n" +
    "				\"mimeType\":\"image/jpeg\",\"latitude\":0.0,\"longitude\":0.0,\"isPrivate\":0,\n" +
    "				</code><br/><code>\n" +
    "				\"orientation\":0,\"id\":26433,\"size\":3119282,\"dateTaken\":-469194825}],\n" +
    "				</code><br/><code>\n" +
    "				\"description\":\"\",\"requestMethod\":\"GET\",\"isSuccessful\":true}\n" +
    "				</code>\n" +
    "				</p>\n" +
    "			</div>\n" +
    "			\n" +
    "			<table id=\"sms_api_doc\" class=\"table table-striped\">\n" +
    "			<thead>\n" +
    "				<tr>\n" +
    "					<th>Field</th>\n" +
    "					<th>Description</th>\n" +
    "				</tr>\n" +
    "			</thead>\n" +
    "			\n" +
    "			<tbody>    	      \n" +
    "			\n" +
    "				<tr id=\"description\">\n" +
    "					<td>\n" +
    "						description\n" +
    "					</td>\n" +
    "				<td>\n" +
    "					Contains the error message if isSuccessful is \"false\".\n" +
    "				</td>\n" +
    "				</tr>\n" +
    "				\n" +
    "				<tr id=\"requestMethod\">\n" +
    "					<td>\n" +
    "						requestMethod\n" +
    "					</td>\n" +
    "				<td>\n" +
    "					Request method. Default to \"GET\", \"POST\" or \"DELETE\" depending on the HTTP method.\n" +
    "				</td>\n" +
    "				</tr>\n" +
    "				\n" +
    "				<tr id=\"requestMethod\">\n" +
    "					<td>\n" +
    "						isSuccessful\n" +
    "					</td>\n" +
    "				<td>\n" +
    "					true if operation is successful. Otherwise returns false.\n" +
    "				</td>\n" +
    "				</tr>\n" +
    "				\n" +
    "				\n" +
    "				<tr id=\"images\">\n" +
    "					<td>\n" +
    "						images\n" +
    "					</td>\n" +
    "					\n" +
    "				<td>\n" +
    "					A list of image objects.\n" +
    "				</td>\n" +
    "				</tr>\n" +
    "				\n" +
    "				<tr id=\"videos\">\n" +
    "					<td>\n" +
    "						videos\n" +
    "					</td>\n" +
    "					\n" +
    "				<td>\n" +
    "					A list of video objects.\n" +
    "				</td>\n" +
    "				</tr>\n" +
    "				\n" +
    "				<tr id=\"audios\">\n" +
    "					<td>\n" +
    "						audios\n" +
    "					</td>\n" +
    "					\n" +
    "				<td>\n" +
    "					A list of audio objects.\n" +
    "				</td>\n" +
    "				</tr>\n" +
    "				\n" +
    "			</tbody>\n" +
    "			</table>			\n" +
    "			\n" +
    "			<div class=\"alert alert-success\">\n" +
    "				<h4 class=\"alert-heading\"> <button class=\"btn btn-default btn-xs\">GET</button> View Media </h4> \n" +
    "				<p></p> <br>\n" +
    "				\n" +
    "				<button type=\"button\" class=\"btn btn-info btn-xs\">Image</button><p class=\"long-wrap\">&nbsp;&nbsp;{{allMediaLink}}image/<b>{id}</b></p>\n" +
    "				<br/><br/>\n" +
    "				<button type=\"button\" class=\"btn btn-info btn-xs\">Video&nbsp;&nbsp;&nbsp;</button><p class=\"long-wrap\">&nbsp;&nbsp;{{allMediaLink}}video/<b>{id}</b></p>\n" +
    "				<br/><br/>\n" +
    "				<button type=\"button\" class=\"btn btn-info btn-xs\">Audio&nbsp;&nbsp;&nbsp;</button><p class=\"long-wrap\">&nbsp;&nbsp;{{allMediaLink}}audio/<b>{id}</b></p>\n" +
    "			</div>\n" +
    "			\n" +
    "			<table class=\"table table-striped\">\n" +
    "				<thead>\n" +
    "					<tr>\n" +
    "						<th>Parameter</th>\n" +
    "						<th>Description</th>\n" +
    "					</tr>\n" +
    "				</thead>\n" +
    "				\n" +
    "				<tbody>    	      \n" +
    "					<tr id=\"id\">\n" +
    "						<td>\n" +
    "							id\n" +
    "						</td>\n" +
    "						<td>\n" +
    "							Media id to retrieve the information.\n" +
    "						</td>\n" +
    "					</tr>\n" +
    "				</tbody>\n" +
    "			</table>\n" +
    "			\n" +
    "			<div class=\"alert alert-warning\">\n" +
    "				<h4 class=\"alert-heading\">Response</h4>\n" +
    "				<p class=\"long-wrap\">\n" +
    "				<code>\n" +
    "				{\"images\":[{\"bucketDisplayName\":\"Camera\",\"bucketId\":\"1509922574\",\n" +
    "				</code><br/><code>\n" +
    "				\"contentUri\":\"content://media/external/images/media/26433\",\n" +
    "				</code><br/><code>\n" +
    "				\"data\":\"/storage/sdcard0/DCIM/Camera/20140510_104903.jpg\",\n" +
    "				</code><br/><code>\n" +
    "				\"width\":\"2560\",\"displayName\":\"20140510_104903.jpg\",\n" +
    "				</code><br/><code>\n" +
    "				\"height\":\"1920\",\"mimeType\":\"image/jpeg\",\"latitude\":0.0,\n" +
    "				</code><br/><code>\n" +
    "				\"longitude\":0.0,\"isPrivate\":0,\"orientation\":0,\"id\":26433,\n" +
    "				</code><br/><code>\n" +
    "				\"size\":3119282,\"dateTaken\":-469194825}],\n" +
    "				</code><br/><code>\n" +
    "				\"description\":\"\",\"requestMethod\":\"GET\",\"isSuccessful\":true}\n" +
    "				</code>\n" +
    "				</p>\n" +
    "			</div>\n" +
    "			\n" +
    "			<div class=\"alert alert-success\">\n" +
    "				<h4 class=\"alert-heading\"> <button class=\"btn btn-default btn-xs\">DELETE</button> Delete Media </h4> \n" +
    "				<p></p> <br>\n" +
    "				\n" +
    "				<button type=\"button\" class=\"btn btn-info btn-xs\">Image</button><p class=\"long-wrap\">&nbsp;&nbsp;{{allMediaLink}}image/<b>{id}</b></p>\n" +
    "				<br/><br/>\n" +
    "				<button type=\"button\" class=\"btn btn-info btn-xs\">Video&nbsp;&nbsp;&nbsp;</button><p class=\"long-wrap\">&nbsp;&nbsp;{{allMediaLink}}video/<b>{id}</b></p>\n" +
    "				<br/><br/>\n" +
    "				<button type=\"button\" class=\"btn btn-info btn-xs\">Audio&nbsp;&nbsp;&nbsp;</button><p class=\"long-wrap\">&nbsp;&nbsp;{{allMediaLink}}audio/<b>{id}</b></p>\n" +
    "			</div>\n" +
    "			\n" +
    "			<table class=\"table table-striped\">\n" +
    "				<thead>\n" +
    "					<tr>\n" +
    "						<th>Parameter</th>\n" +
    "						<th>Description</th>\n" +
    "					</tr>\n" +
    "				</thead>\n" +
    "				\n" +
    "				<tbody>    	      \n" +
    "					<tr id=\"id\">\n" +
    "						<td>\n" +
    "							id\n" +
    "						</td>\n" +
    "						<td>\n" +
    "							Media id to delete. Compulsory value which must be provided.\n" +
    "						</td>\n" +
    "					</tr>\n" +
    "				</tbody>\n" +
    "			</table>\n" +
    "			\n" +
    "				<div class=\"alert alert-warning\">\n" +
    "				<h4 class=\"alert-heading\">Response</h4>\n" +
    "				<p class=\"long-wrap\">\n" +
    "				<code>\n" +
    "				{\n" +
    "					\"count\": 1,\n" +
    "					\"description\": \"Successfully deleted the media with id 2176\",\n" +
    "				</code><br/><code>\n" +
    "					\"requestMethod\": \"DELETE\",\n" +
    "					\"isSuccessful\": true\n" +
    "				}</code>\n" +
    "				</p>\n" +
    "			</div>\n" +
    "			\n" +
    "			<table class=\"table table-striped\">\n" +
    "			<thead>\n" +
    "				<tr>\n" +
    "					<th>Field</th>\n" +
    "					<th>Description</th>\n" +
    "				</tr>\n" +
    "			</thead>\n" +
    "			\n" +
    "			<tbody>    	      \n" +
    "				<tr>\n" +
    "					<td>\n" +
    "						count\n" +
    "					</td>\n" +
    "					\n" +
    "				<td>\n" +
    "					Number of deleted media.							\n" +
    "				</td>\n" +
    "				</tr>\n" +
    "			</tbody>\n" +
    "			</table>\n" +
    "			\n" +
    "			\n" +
    "			<div class=\"alert alert-success\">\n" +
    "				<h4 class=\"alert-heading\"> <button class=\"btn btn-default btn-xs\">GET</button> Stream Media </h4> \n" +
    "				<p></p> <br>\n" +
    "				\n" +
    "				<button type=\"button\" class=\"btn btn-info btn-xs\">Image</button><p class=\"long-wrap\">&nbsp;&nbsp;{{streamMediaLink}}image?uri=<b>{uri}</b>&id=<b>{id}</b>&kind=<b>{kind}</b></p>\n" +
    "				<br/><br/>\n" +
    "				<button type=\"button\" class=\"btn btn-info btn-xs\">Video&nbsp;&nbsp;&nbsp;</button><p class=\"long-wrap\">&nbsp;&nbsp;{{streamMediaLink}}video?uri=<b>{uri}</b>&id=<b>{id}</b>&kind=<b>{kind}</b></p>\n" +
    "				<br/><br/>\n" +
    "				<button type=\"button\" class=\"btn btn-info btn-xs\">Audio&nbsp;&nbsp;&nbsp;</button><p class=\"long-wrap\">&nbsp;&nbsp;{{streamMediaLink}}audio?uri=<b>{uri}</b>&id=<b>{id}</b>&kind=<b>{kind}</b></p>\n" +
    "			</div>\n" +
    "			\n" +
    "				\n" +
    "			<table id=\"sms_api_doc\" class=\"table table-striped\">\n" +
    "				<thead>\n" +
    "					<tr>\n" +
    "						<th>Parameter</th>\n" +
    "						<th>Description</th>\n" +
    "					</tr>\n" +
    "				</thead>\n" +
    "				\n" +
    "				<tbody>    	      \n" +
    "					<tr id=\"uri\">\n" +
    "						<td>\n" +
    "							uri\n" +
    "						</td>\n" +
    "					<td>\n" +
    "						Content URI for the media, e.g. content://media/external/images/media/26423\n" +
    "					</td>\n" +
    "					</tr>\n" +
    "					\n" +
    "					<tr id=\"id\">\n" +
    "						<td>\n" +
    "							id\n" +
    "						</td>\n" +
    "						<td>\n" +
    "							Media id, e.g. 26423.\n" +
    "						</td>\n" +
    "					</tr>\n" +
    "					\n" +
    "					<tr id=\"kind\">\n" +
    "						<td>\n" +
    "							kind\n" +
    "						</td>\n" +
    "						<td>\n" +
    "							Determine how the media is displayed.\n" +
    "							\n" +
    "							<dl class=\"dl-horizontal\">\n" +
    "							  <dt>0&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</dt>\n" +
    "							  <dd>Image - display at the original size. <br/> Video - stream the video. <br/>\n" +
    "								  Audio - stream the audio.<br> </dd>\n" +
    "							  <dt>1&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</dt>\n" +
    "							  <dd>Image - display the micro thumbnail. <br/> Video - display the micro thumbnail. <br/>\n" +
    "								  Audio - display the album art.</dd>\n" +
    "							  <dt>2&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</dt>\n" +
    "							 <dd>Image - display the mini thumbnail. <br/> Video - display the mini thumbnail. <br/>\n" +
    "								  Audio - display the album art.</dd>\n" +
    "							</dl>\n" +
    "						</td>\n" +
    "					</tr>\n" +
    "				</tbody>\n" +
    "			</table>\n" +
    "			\n" +
    "			\n" +
    "			<div class=\"alert alert-info\">\n" +
    "			\n" +
    "				<p>\n" +
    "				<h4 class=\"alert-heading\">Usage - <i>Image Gallery</i> </h3>\n" +
    "				</p> <br/>				\n" +
    "				  <ul class=\"row\">\n" +
    "					<li class=\"col-lg-2 col-md-2 col-sm-3 col-xs-4\" ng-repeat=\"image in images\" style=\"margin-bottom:25px\">\n" +
    "						<img class=\"img-responsive\" ng-src=\"{{streamImageLink}}?uri={{image.contentUri}}&&id={{image.id}}&kind=1\" ng-click=\"showImage($index)\" uib-tooltip-placement=\"top\" uib-tooltip=\"{{image.displayName}}\"  >\n" +
    "					</li>\n" +
    "				  </ul>\n" +
    "				\n" +
    "			</div>\n" +
    "			\n" +
    "			<div class=\"alert alert-info\">\n" +
    "				<p>\n" +
    "				<h4 class=\"alert-heading\">Usage - <i>Video Gallery</i> </h3>\n" +
    "				</p> <br/>				\n" +
    "				  <ul class=\"row\">\n" +
    "					<li class=\"col-lg-2 col-md-2 col-sm-3 col-xs-4\" ng-repeat=\"video in videos\" style=\"margin-bottom:25px\">\n" +
    "						<img class=\"img-responsive\" ng-src=\"{{streamVideoLink}}?uri={{video.contentUri}}&&id={{video.id}}&kind=1\" ng-click=\"showVideo($index)\" uib-tooltip-placement=\"top\" uib-tooltip=\"{{video.displayName}}\" >\n" +
    "					</li>\n" +
    "				  </ul>\n" +
    "				\n" +
    "			</div>\n" +
    "			\n" +
    "			\n" +
    "			\n" +
    "		</div>\n" +
    "	</div>	\n" +
    "</div>\n" +
    "	\n" +
    "");
}]);

angular.module("messaging/messaging.tpl.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("messaging/messaging.tpl.html",
    "<div class=\"container-fluid\">\n" +
    "	<div class=\"row\">\n" +
    "		<div class=\"col-md-3\" id=\"sidebar\" ng-include=\"'assets/view/services_sidebar.html'\"></div>\n" +
    "		\n" +
    "		\n" +
    "		<div class=\"col-md-9\" id=\"content\">\n" +
    "			\n" +
    "			<div class=\"alert alert-info\">\n" +
    "				<h3 class=\"alert-heading\">SMS API</h3>\n" +
    "			</div>\n" +
    "			\n" +
    "			<div class=\"alert alert-warning\">\n" +
    "			<strong>\n" +
    "				For Android 4.4 KitKat and above, in order for SMS update/delete to work, myMobKit has to be configured\n" +
    "				as the default SMS app. This can be configured through myMobKit menu or Android settings.\n" +
    "			</strong>\n" +
    "			</div>\n" +
    "			\n" +
    "			<div class=\"alert alert-success\">\n" +
    "				<h4 class=\"alert-heading\"> <button class=\"btn btn-default btn-xs\">POST</button> Configuration Parameters </h4> \n" +
    "				<p class=\"long-wrap\">\n" +
    "				{{parameterLink}}\n" +
    "				</p>\n" +
    "				\n" +
    "				<br/><br/>\n" +
    "				\n" +
    "				<form class=\"form-horizontal\" role=\"form\" ng-submit=\"submitMessagingForm()\">\n" +
    "				  <div class=\"form-group\">\n" +
    "					<label for=\"preferences_messaging_aging_method\" class=\"col-sm-4 control-label\">Housekeeping</label>\n" +
    "					<div class=\"col-sm-3\">\n" +
    "						<select class=\"form-control\" id=\"preferences_messaging_aging_method\" ng-model=\"messagingForm.preferences_messaging_aging_method\">\n" +
    "							  <option value=\"Days\">Days</option>\n" +
    "							  <option value=\"Size\">Size</option>\n" +
    "						</select>\n" +
    "					</div>\n" +
    "				  </div>\n" +
    "				  <div class=\"form-group\">\n" +
    "					<label for=\"preferences_messaging_aging_days\" class=\"col-sm-4 control-label\">Aging days</label>\n" +
    "					<div class=\"col-sm-3\">\n" +
    "					  <input type=\"number\" class=\"form-control\" ng-model=\"messagingForm.preferences_messaging_aging_days\" id=\"preferences_messaging_aging_days\" ng-minlength=\"1\" ng-maxlength=\"30\" min=\"1\" max=\"30\" placeholder=\"Aging days\"/>\n" +
    "					</div>\n" +
    "				  </div>\n" +
    "				  <div class=\"form-group\">\n" +
    "					<label for=\"preferences_messaging_aging_size\" class=\"col-sm-4 control-label\">Number of messages</label>\n" +
    "					<div class=\"col-sm-3\">\n" +
    "					  <input type=\"number\" class=\"form-control\" ng-model=\"messagingForm.preferences_messaging_aging_size\" id=\"preferences_messaging_aging_size\" ng-minlength=\"0\" ng-maxlength=\"1000\" min=\"0\" max=\"1000\" placeholder=\"Number of messages\"/>\n" +
    "					</div>\n" +
    "				  </div>\n" +
    "				  <div class=\"form-group\">\n" +
    "					<div class=\"col-sm-offset-4 col-sm-10\">\n" +
    "					  <div class=\"checkbox\">\n" +
    "						<label>\n" +
    "						  <input type=\"checkbox\" ng-model=\"messagingForm.preferences_saved_sent_messages\"  id=\"preferences_saved_sent_messages\" ng-true-value=\"'true'\" ng-false-value=\"'false'\" /> Save sent messages\n" +
    "						</label>\n" +
    "					  </div>\n" +
    "					</div>\n" +
    "				  </div>\n" +
    "				  <div class=\"form-group\">\n" +
    "					<div class=\"col-sm-offset-4 col-sm-10\">\n" +
    "					  <button type=\"submit\" class=\"btn btn-default\" ng-disabled=\"messageFormStatus\">Save</button>\n" +
    "					</div>\n" +
    "				  </div>\n" +
    "				</form>\n" +
    "				\n" +
    "				<br/>\n" +
    "				{{ saveStatus}}\n" +
    "			</div>\n" +
    "			\n" +
    "			\n" +
    "			<table id=\"sms_api_doc\" class=\"table table-striped\">\n" +
    "				<thead>\n" +
    "					<tr>\n" +
    "						<th>Parameter</th>\n" +
    "						<th>Description</th>\n" +
    "					</tr>\n" +
    "				</thead>\n" +
    "				\n" +
    "				<tbody>    	      \n" +
    "					<tr id=\"preferences_messaging_aging_method\">\n" +
    "						<td>\n" +
    "							preferences_messaging_aging_method\n" +
    "						</td>\n" +
    "					<td>\n" +
    "						Housekeeping the internal message storage either by \"Days\" or \"Size\" (number of records).\n" +
    "					</td>\n" +
    "					</tr>\n" +
    "					\n" +
    "					<tr id=\"preferences_messaging_aging_days\">\n" +
    "						<td>\n" +
    "							preferences_messaging_aging_days\n" +
    "						</td>\n" +
    "						<td>\n" +
    "							Number of days to keep the messages in the internal storage, range from 1 to 30 days.\n" +
    "						</td>\n" +
    "					</tr>\n" +
    "					\n" +
    "					<tr id=\"preferences_messaging_aging_size\">\n" +
    "						<td>\n" +
    "							preferences_messaging_aging_size\n" +
    "						</td>\n" +
    "						<td>\n" +
    "							Number of records to keep, range from 0 to 1000.\n" +
    "						</td>\n" +
    "					</tr>\n" +
    "					\n" +
    "					<tr id=\"preferences_saved_sent_messages\">\n" +
    "						<td>\n" +
    "							preferences_saved_sent_messages\n" +
    "						</td>\n" +
    "						<td>\n" +
    "							Save the sent messages in device sent folder.\n" +
    "						</td>\n" +
    "					</tr>\n" +
    "				</tbody>\n" +
    "			</table>\n" +
    "			\n" +
    "			<div class=\"alert alert-warning\">\n" +
    "				<h4 class=\"alert-heading\">Response</h4>\n" +
    "			\n" +
    "				<p class=\"long-wrap\">\n" +
    "				<code>\n" +
    "				{\n" +
    "					\"description\": \"\",\n" +
    "					\"requestMethod\": \"POST\",\n" +
    "					\"isSuccessful\": true\n" +
    "				}\n" +
    "				</code>\n" +
    "				</p>\n" +
    "			</div>\n" +
    "			\n" +
    "			<table id=\"sms_api_doc\" class=\"table table-striped\">\n" +
    "			<thead>\n" +
    "				<tr>\n" +
    "					<th>Field</th>\n" +
    "					<th>Description</th>\n" +
    "				</tr>\n" +
    "			</thead>\n" +
    "			\n" +
    "			<tbody>    	      \n" +
    "				<tr id=\"description\">\n" +
    "					<td>\n" +
    "						description\n" +
    "					</td>\n" +
    "				<td>\n" +
    "					Contains the error message if isSuccessful is \"false\".\n" +
    "				</td>\n" +
    "				</tr>\n" +
    "				\n" +
    "				<tr id=\"requestMethod\">\n" +
    "					<td>\n" +
    "						requestMethod\n" +
    "					</td>\n" +
    "				<td>\n" +
    "					Request method. Default to \"GET\", \"POST\" or \"DELETE\" depending on the HTTP method.\n" +
    "				</td>\n" +
    "				</tr>\n" +
    "				\n" +
    "				<tr id=\"requestMethod\">\n" +
    "					<td>\n" +
    "						isSuccessful\n" +
    "					</td>\n" +
    "				<td>\n" +
    "					true if operation is successful. Otherwise returns false.\n" +
    "				</td>\n" +
    "				</tr>\n" +
    "				\n" +
    "				\n" +
    "			</tbody>\n" +
    "			</table>\n" +
    "			\n" +
    "			\n" +
    "			<br/><br/>\n" +
    "			\n" +
    "			<div class=\"alert alert-success\">\n" +
    "				<h4 class=\"alert-heading\"> <button class=\"btn btn-default btn-xs\">GET</button> List SMS </h4> \n" +
    "				<p></p> <br>\n" +
    "				<p class=\"long-wrap\">\n" +
    "				<a ng-href=\"{{allMessagesLink}}\">{{allMessagesLink}}</a>?to=<b>{to}</b>&from=<b>{from}</b>&datesent=<b>{datesent}</b>&page=<b>{page}</b>&pagesize=<b>{pagesize}</b>&threadid=<b>{threadid}</b>&status=<b>{status}</b>\n" +
    "				</p>\n" +
    "			</div>\n" +
    "			\n" +
    "			<table id=\"sms_api_doc\" class=\"table table-striped\">\n" +
    "				<thead>\n" +
    "					<tr>\n" +
    "						<th>Parameter</th>\n" +
    "						<th>Description</th>\n" +
    "					</tr>\n" +
    "				</thead>\n" +
    "				\n" +
    "				<tbody>    	      \n" +
    "					<tr id=\"To\">\n" +
    "						<td>\n" +
    "							To\n" +
    "						</td>\n" +
    "					<td>\n" +
    "						Lists all SMS messages sent to this number.\n" +
    "					</td>\n" +
    "					</tr>\n" +
    "					\n" +
    "					<tr id=\"From\">\n" +
    "						<td>\n" +
    "							From\n" +
    "						</td>\n" +
    "					<td>\n" +
    "						Lists all SMS messages sent from this number.\n" +
    "					</td>\n" +
    "					</tr>\n" +
    "					\n" +
    "					<tr id=\"DateSent\">\n" +
    "						<td>\n" +
    "							DateSent\n" +
    "						</td>\n" +
    "					<td>\n" +
    "						Lists all SMS messages sent beginning on or from a certain date. \n" +
    "						<p>\n" +
    "						Date range can be specified using inequalities like DateSent=<b>>=YYYY-MM-DD</b> or DateSent=<b><=YYYY-MM-DD</b>.\n" +
    "						</p>\n" +
    "						<p>\n" +
    "						Accepted symbols are \"=\", \">\", \">=\", \"<\" and \"<=\".\n" +
    "						</p>\n" +
    "						<p>\n" +
    "						Sample URL patterns are shown below<br/>\n" +
    "						<br/>\n" +
    "						Greater than \"2016-02-14\"\n" +
    "						<br/>/services/api/messaging/?datesent=<b>>=2016-02-14</b>\n" +
    "						<br/><br/>\n" +
    "						Equal to \"2016-02-14\"\n" +
    "						<br/>/services/api/messaging/?datesent=<b>=2016-02-14</b>\n" +
    "						</p>\n" +
    "					</td>\n" +
    "					</tr>\n" +
    "					\n" +
    "					<tr id=\"Page\">\n" +
    "						<td>\n" +
    "							Page\n" +
    "						</td>\n" +
    "					<td>\n" +
    "						Used to return a particular page within the list. Must be used together with the PageSize parameter.\n" +
    "					</td>\n" +
    "					</tr>\n" +
    "					\n" +
    "					<tr id=\"PageSize\">\n" +
    "						<td>\n" +
    "							PageSize\n" +
    "						</td>\n" +
    "					<td>\n" +
    "						Used to specify the amount of list items to return per page.\n" +
    "					</td>\n" +
    "					</tr>\n" +
    "					\n" +
    "					<tr id=\"ThreadID\">\n" +
    "						<td>\n" +
    "							ThreadID\n" +
    "						</td>\n" +
    "					<td>\n" +
    "						Used to retrieve messages belonging to the same conversation.\n" +
    "					</td>\n" +
    "					</tr>\n" +
    "					\n" +
    "					<tr id=\"Status\">\n" +
    "						<td>\n" +
    "							Status\n" +
    "						</td>\n" +
    "					<td>\n" +
    "						0 for unread messages, 1 for read messages. Other values will be ignored.\n" +
    "					</td>\n" +
    "					</tr>\n" +
    "				</tbody>\n" +
    "			</table>\n" +
    "	\n" +
    "			\n" +
    "			<div class=\"alert alert-warning\">\n" +
    "				<h4 class=\"alert-heading\">Response</h4>\n" +
    "				<p class=\"long-wrap\">\n" +
    "				<code>\n" +
    "				{\"messages\":[{\"date\":\"2014-11-22T18:13:01GMT+08:00\",\"id\":\"2175\",\"message\":\"hi how are you?\",\n" +
    "				</code><br/><code>\n" +
    "				\"messageType\":\"MESSAGE_TYPE_INBOX\",\"number\":\"+1234567890\",\"receiver\":\"Me\",\"sender\":\"Alice\"\n" +
    "				</code><br/><code>\n" +
    "				\"threadID\":301, \"read\":true}],\n" +
    "				</code><br/><code>\n" +
    "				\"description\":\"\",\"requestMethod\":\"GET\",\"isSuccessful\":true}\n" +
    "				</code>\n" +
    "				</p>\n" +
    "			</div>\n" +
    "			\n" +
    "			\n" +
    "			<table id=\"sms_api_doc\" class=\"table table-striped\">\n" +
    "			<thead>\n" +
    "				<tr>\n" +
    "					<th>Field</th>\n" +
    "					<th>Description</th>\n" +
    "				</tr>\n" +
    "			</thead>\n" +
    "			\n" +
    "			<tbody>    	      \n" +
    "				<tr id=\"messages\">\n" +
    "					<td>\n" +
    "						messages\n" +
    "					</td>\n" +
    "					\n" +
    "				<td>\n" +
    "					A list of message object with the following fields\n" +
    "					<br/><br/>\n" +
    "					\n" +
    "					<dl class=\"dl-horizontal\">\n" +
    "					  <dt>date</dt>\n" +
    "					  <dd>Message timestamp.</dd>\n" +
    "					  <dt>id</dt>\n" +
    "					  <dd>Unique message id.</dd>\n" +
    "					  <dt>message</dt>\n" +
    "					  <dd>Message content.</dd>\n" +
    "					  <dt>messageType</dt>\n" +
    "					  <dd>Message type which can be MESSAGE_TYPE_INBOX, MESSAGE_TYPE_SENT, MESSAGE_TYPE_OUTBOX, MESSAGE_TYPE_FAILED or MESSAGE_TYPE_QUEUED.</dd>\n" +
    "					  <dt>number</dt>\n" +
    "					  <dd>Phone number of the sender or receiver.</dd>\n" +
    "					  <dt>receiver</dt>\n" +
    "					  <dd>Receiver name or number.</dd>\n" +
    "					  <dt>sender</dt>\n" +
    "					  <dd>Sender name or number.</dd>\n" +
    "					  <dt>threadID</dt>\n" +
    "					  <dd>Message thread ID to identify the grouping of the messages.</dd>\n" +
    "					  <dt>read</dt>\n" +
    "					  <dd>true if message is read.</dd>\n" +
    "					  <dt>serviceCenter</dt>\n" +
    "					  <dd>Service center address.</dd>\n" +
    "					</dl>\n" +
    "				</td>\n" +
    "				</tr>\n" +
    "				\n" +
    "			</tbody>\n" +
    "			</table>			\n" +
    "			\n" +
    "			\n" +
    "			<div class=\"alert alert-success\">\n" +
    "				<h4 class=\"alert-heading\"> <button class=\"btn btn-default btn-xs\">GET</button> View SMS </h4>\n" +
    "				<br/>\n" +
    "				<p class=\"long-wrap\">\n" +
    "				{{allMessagesLink}}<b>{id}</b>\n" +
    "				</p>	\n" +
    "			</div>\n" +
    "			\n" +
    "			\n" +
    "			<div class=\"alert alert-warning\">\n" +
    "				<h4 class=\"alert-heading\">Response</h4>\n" +
    "				<p class=\"long-wrap\">\n" +
    "				<code>\n" +
    "				{\"messages\":[{\"date\":\"2014-11-22T18:13:01GMT+08:00\",\"id\":\"2103\",\"message\":\"How are you doing?\",</code>\n" +
    "				<br/>\n" +
    "				<code>\"messageType\":\"MESSAGE_TYPE_SENT\",\"number\":\"1234567890\",\"receiver\":\"Alice\", </code><br/><code>\n" +
    "				\"sender\":\"Me\",\"threadID\":313,\"read\":true}], </code><br/><code>\"description\":\"\",\"requestMethod\":\"GET\",\"isSuccessful\":true}</code>\n" +
    "				</p>\n" +
    "			</div>\n" +
    "			\n" +
    "			\n" +
    "			<div class=\"alert alert-success\">				\n" +
    "				<h4 class=\"alert-heading\"> <button class=\"btn btn-default btn-xs\">POST</button> Send SMS </h4>\n" +
    "				<br/>\n" +
    "				<p class=\"long-wrap\">\n" +
    "				{{allMessagesLink}}	\n" +
    "				</p>\n" +
    "			</div>\n" +
    "			\n" +
    "			<table id=\"sms_api_doc\" class=\"table table-striped\">\n" +
    "				<thead>\n" +
    "					<tr>\n" +
    "						<th>Parameter</th>\n" +
    "						<th>Description</th>\n" +
    "					</tr>\n" +
    "				</thead>\n" +
    "				\n" +
    "				<tbody>    	      \n" +
    "					<tr id=\"To\">\n" +
    "						<td>\n" +
    "							To\n" +
    "						</td>\n" +
    "						<td>\n" +
    "							The destination number or contact name.\n" +
    "						</td>\n" +
    "					</tr>\n" +
    "					\n" +
    "					<tr id=\"To\">\n" +
    "						<td>\n" +
    "							Message\n" +
    "						</td>\n" +
    "						<td>\n" +
    "							Message content.\n" +
    "						</td>\n" +
    "					</tr>\n" +
    "					\n" +
    "					<tr>\n" +
    "						<td>\n" +
    "							DeliveryReport\n" +
    "						</td>\n" +
    "						<td>\n" +
    "							Optional parameter. 0 for no delivery report, any other values indicate delivery report is required. \n" +
    "							Default to 1 to receive delivery report.\n" +
    "						</td>\n" +
    "					</tr>\n" +
    "					\n" +
    "					<tr>\n" +
    "						<td>\n" +
    "							ScAddress\n" +
    "						</td>\n" +
    "						<td>\n" +
    "							Optional parameter to specify the service center address to be used to send SMS. <br/><br/>\n" +
    "							\n" +
    "							For dual SIMs with different providers, you can provide this address to use a specific SIM card to send SMS.\n" +
    "						</td>\n" +
    "					</tr>\n" +
    "					\n" +
    "					<tr>\n" +
    "						<td>\n" +
    "							Slot \n" +
    "						</td>\n" +
    "						<td>\n" +
    "							Optional parameter to specify the SIM card to be used to send SMS. Only applicable for dual SIM phone. <br/><br/>\n" +
    "							1 for SIM slot 1, 2 for SIM slot 2. Default to the default configured active network.	\n" +
    "							\n" +
    "							<br/><br/>\n" +
    "							Use the ScAddress parameter to target a specific SIM card. Use this parameter only when the service center address is different between the 2 SIM cards.						\n" +
    "						</td>\n" +
    "					</tr>\n" +
    "					\n" +
    "					\n" +
    "				</tbody>\n" +
    "			</table>\n" +
    "			\n" +
    "			\n" +
    "			<div class=\"alert alert-warning\">\n" +
    "				<h4 class=\"alert-heading\">Response</h4>\n" +
    "				<p class=\"long-wrap\">\n" +
    "				<code>\n" +
    "				{\n" +
    "					\"message\": {\n" +
    "						\"date\": \"2014-11-22T18:13:01GMT+08:00\",\n" +
    "						\"to\": \"Alice\",\n" +
    "						\"id\": \"7754325357517385744\",\n" +
    "				</code><br/><code>\n" +
    "						\"number\": \"1234567890\",	\n" +
    "						\"message\": \"how are you?\",\n" +
    "						\"read\": false						\n" +
    "				</code><br/><code>\n" +
    "					},\n" +
    "					\"description\": \"\",\n" +
    "					\"requestMethod\": \"POST\",\n" +
    "					\"isSuccessful\": true\n" +
    "				}\n" +
    "			   </code>\n" +
    "				</p>\n" +
    "			</div>\n" +
    "			\n" +
    "			<table id=\"sms_api_doc\" class=\"table table-striped\">\n" +
    "			<thead>\n" +
    "				<tr>\n" +
    "					<th>Field</th>\n" +
    "					<th>Description</th>\n" +
    "				</tr>\n" +
    "			</thead>\n" +
    "			\n" +
    "			<tbody>    	      \n" +
    "				<tr id=\"message\">\n" +
    "					<td>\n" +
    "						message\n" +
    "					</td>\n" +
    "					\n" +
    "				<td>\n" +
    "					Message object containing the following fields for the sent message.\n" +
    "					<br/><br/>\n" +
    "					\n" +
    "					<dl class=\"dl-horizontal\">\n" +
    "					  <dt>date</dt>\n" +
    "					  <dd>Message timestamp.</dd>\n" +
    "					  <dt>to</dt>\n" +
    "					  <dd>Receiver name or number.</dd>\n" +
    "					  <dt>id</dt>\n" +
    "					  <dd>A <b>unique identifier</b> that can be used to check the message status.</dd>\n" +
    "					  <dt>number</dt>\n" +
    "					  <dd>Phone number of the receiver.</dd>					  \n" +
    "					</dl>					\n" +
    "				</td>\n" +
    "				</tr>\n" +
    "				\n" +
    "			</tbody>\n" +
    "			</table>\n" +
    "			\n" +
    "			\n" +
    "			<div class=\"alert alert-success\">\n" +
    "				<h4 class=\"alert-heading\"> <button class=\"btn btn-default btn-xs\">GET</button> Check SMS Status </h4>\n" +
    "				<br/>\n" +
    "				<p class=\"long-wrap\">\n" +
    "				{{allMessagesLink}}status/<b>{unique identifier}</b>\n" +
    "				</p>	\n" +
    "			</div>\n" +
    "			\n" +
    "			<div class=\"alert alert-warning\">\n" +
    "				<h4 class=\"alert-heading\">Response</h4>\n" +
    "				<p class=\"long-wrap\">\n" +
    "				<code>\n" +
    "				{\"message\":{\"answerTo\":\"unknown\",\"date\":\"2014-11-22T18:13:01GMT+08:00\",</code><br/><code>\"to\":\"Alice\",\"id\":\"7754325357517385744\",\n" +
    "				\"number\":\"1234567890\"},\"status\":\"Failed\",</code><br/><code>\"description\":\"\",\"requestMethod\":\"GET\",\"isSuccessful\":true} </code>\n" +
    "				</p>\n" +
    "			</div>\n" +
    "			\n" +
    "				<table id=\"sms_api_doc\" class=\"table table-striped\">\n" +
    "			<thead>\n" +
    "				<tr>\n" +
    "					<th>Field</th>\n" +
    "					<th>Description</th>\n" +
    "				</tr>\n" +
    "			</thead>\n" +
    "			\n" +
    "			<tbody>    	      \n" +
    "				<tr id=\"message\">\n" +
    "					<td>\n" +
    "						message\n" +
    "					</td>\n" +
    "					\n" +
    "				<td>\n" +
    "					Message object containing the following fields for the sent message.\n" +
    "					<br/><br/>\n" +
    "					\n" +
    "					<dl class=\"dl-horizontal\">\n" +
    "					  <dt>answerTo</dt>\n" +
    "					  <dd>Reply name or number.</dd>\n" +
    "					  <dt>date</dt>\n" +
    "					  <dd>Message timestamp</dd>\n" +
    "					  <dt>to</dt>\n" +
    "					  <dd>Receiver name or number.</dd>\n" +
    "					  <dt>id</dt>\n" +
    "					  <dd>Message unique identifier.</dd>		\n" +
    "					  <dt>number</dt>\n" +
    "					  <dd>Receiver number.</dd>							  \n" +
    "					</dl>\n" +
    "				</td>\n" +
    "				</tr>\n" +
    "				\n" +
    "				<tr id=\"status\">\n" +
    "					<td>\n" +
    "						status\n" +
    "					</td>\n" +
    "					\n" +
    "				<td>\n" +
    "					Messaging sending status, which can be \"Sent\", \"Delivered\", \"Queued\" or \"Failed\".\n" +
    "				</td>\n" +
    "				</tr>\n" +
    "				\n" +
    "				<tr id=\"description\">\n" +
    "					<td>\n" +
    "						description\n" +
    "					</td>\n" +
    "					\n" +
    "				<td>\n" +
    "					Contains the error code if the status is \"Failed\".\n" +
    "					\n" +
    "					<br/><br/>\n" +
    "					\n" +
    "					<dl class=\"dl-horizontal\">\n" +
    "					  <dt>0</dt>\n" +
    "					  <dd>Operation canceled.</dd>		\n" +
    "					  <dt>1</dt>\n" +
    "					  <dd>Generic failure.</dd>\n" +
    "					  <dt>2</dt>\n" +
    "					  <dd>Network/radio turned off explicitly.</dd>\n" +
    "					  <dt>3</dt>\n" +
    "					  <dd>No PDU provided.</dd>\n" +
    "					  <dt>4</dt>\n" +
    "					  <dd>Service is currently unavailable.</dd>	\n" +
    "					  <dt>5</dt>\n" +
    "					  <dd>Reached the sending queue limit.</dd>	\n" +
    "					  <dt>6</dt>\n" +
    "					  <dd>FDN (Fixed Dialing Number) is enabled.</dd>						  \n" +
    "					</dl>\n" +
    "					\n" +
    "				</td>\n" +
    "				</tr>\n" +
    "				\n" +
    "				\n" +
    "			</tbody>\n" +
    "			</table>\n" +
    "			\n" +
    "			\n" +
    "			\n" +
    "			<div class=\"alert alert-success\">				\n" +
    "				<h4 class=\"alert-heading\"> <button class=\"btn btn-default btn-xs\">PUT</button> Update SMS Read Status</h4>\n" +
    "				<br/>\n" +
    "				<p class=\"long-wrap\">\n" +
    "				{{allMessagesLink}}<b>{id}</b>\n" +
    "				</p>\n" +
    "				<br/>\n" +
    "				<p class=\"long-wrap\">\n" +
    "				{{allMessagesLink}}<b>?id={id_list}</b>\n" +
    "				</p>	\n" +
    "			</div>\n" +
    "			\n" +
    "			<table id=\"sms_api_doc\" class=\"table table-striped\">\n" +
    "				<thead>\n" +
    "					<tr>\n" +
    "						<th>Parameter</th>\n" +
    "						<th>Description</th>\n" +
    "					</tr>\n" +
    "				</thead>\n" +
    "				\n" +
    "				<tbody>    	      \n" +
    "					<tr id=\"id\">\n" +
    "						<td>\n" +
    "							id\n" +
    "						</td>\n" +
    "						<td>\n" +
    "							Message id to update. If not provided then all unread messages are updated to READ status.\n" +
    "						</td>\n" +
    "					</tr>\n" +
    "					<tr id=\"id_list\">\n" +
    "						<td>\n" +
    "							id_list\n" +
    "						</td>\n" +
    "						<td>\n" +
    "							List of message ids separated by comma.\n" +
    "						</td>\n" +
    "					</tr>\n" +
    "				</tbody>\n" +
    "			</table>\n" +
    "			\n" +
    "			\n" +
    "			<div class=\"alert alert-warning\">\n" +
    "				<h4 class=\"alert-heading\">Response</h4>\n" +
    "				<p class=\"long-wrap\">\n" +
    "				<code>\n" +
    "				{\n" +
    "					\"count\": 3,\n" +
    "					\"description\": \"Updated all unread messages to read\",\n" +
    "				</code><br/><code>\n" +
    "					\"requestMethod\": \"PUT\",\n" +
    "					\"isSuccessful\": true\n" +
    "				}\n" +
    "				</code>\n" +
    "				</p>\n" +
    "\n" +
    "			</div>\n" +
    "			\n" +
    "			<table id=\"sms_api_doc\" class=\"table table-striped\">\n" +
    "			<thead>\n" +
    "				<tr>\n" +
    "					<th>Field</th>\n" +
    "					<th>Description</th>\n" +
    "				</tr>\n" +
    "			</thead>\n" +
    "			\n" +
    "			<tbody>    	      \n" +
    "				<tr id=\"message\">\n" +
    "					<td>\n" +
    "						count\n" +
    "					</td>\n" +
    "					\n" +
    "				<td>\n" +
    "					Number of updated messages.							\n" +
    "				</td>\n" +
    "				</tr>\n" +
    "				\n" +
    "			</tbody>\n" +
    "			</table>\n" +
    "			\n" +
    "			\n" +
    "			\n" +
    "			\n" +
    "			<div class=\"alert alert-success\">				\n" +
    "				<p>\n" +
    "				<h4 class=\"alert-heading\"> <button class=\"btn btn-default btn-xs\">DELETE</button> Delete SMS </h3>\n" +
    "				</p> <br/>\n" +
    "				<p class=\"long-wrap\">\n" +
    "				{{allMessagesLink}}<b>{id}</b>\n" +
    "				</p>	\n" +
    "				<br/>\n" +
    "				<p class=\"long-wrap\">\n" +
    "				{{allMessagesLink}}<b>?id={id_list}</b>\n" +
    "				</p>	\n" +
    "				<br/>\n" +
    "				<p class=\"long-wrap\">\n" +
    "				{{allMessagesLink}}<b>?threadid={thread_id}</b>\n" +
    "				</p>	\n" +
    "			</div>\n" +
    "			\n" +
    "			<table id=\"sms_api_doc\" class=\"table table-striped\">\n" +
    "				<thead>\n" +
    "					<tr>\n" +
    "						<th>Parameter</th>\n" +
    "						<th>Description</th>\n" +
    "					</tr>\n" +
    "				</thead>\n" +
    "				\n" +
    "				<tbody>    	      \n" +
    "					<tr id=\"id\">\n" +
    "						<td>\n" +
    "							id\n" +
    "						</td>\n" +
    "						<td>\n" +
    "							Message id to delete. Compulsory value which must be provided.\n" +
    "						</td>\n" +
    "					</tr>\n" +
    "					<tr id=\"id_list\">\n" +
    "						<td>\n" +
    "							id_list\n" +
    "						</td>\n" +
    "						<td>\n" +
    "							List of message ids separated by comma.\n" +
    "						</td>\n" +
    "					</tr>\n" +
    "					<tr id=\"thread_id\">\n" +
    "						<td>\n" +
    "							thread_id\n" +
    "						</td>\n" +
    "						<td>\n" +
    "							Message thread id to delete.\n" +
    "						</td>\n" +
    "					</tr>\n" +
    "				</tbody>\n" +
    "			</table>\n" +
    "			\n" +
    "			\n" +
    "			<div class=\"alert alert-warning\">\n" +
    "				<h4 class=\"alert-heading\">Response</h4>\n" +
    "				<p class=\"long-wrap\">\n" +
    "				<code>\n" +
    "				{\n" +
    "					\"count\": 1,\n" +
    "					\"description\": \"Successfully deleted the message with id 2176\",\n" +
    "				</code><br/><code>\n" +
    "					\"requestMethod\": \"DELETE\",\n" +
    "					\"isSuccessful\": true\n" +
    "				}</code>\n" +
    "				</p>\n" +
    "			</div>\n" +
    "			\n" +
    "			\n" +
    "			<table id=\"sms_api_doc\" class=\"table table-striped\">\n" +
    "			<thead>\n" +
    "				<tr>\n" +
    "					<th>Field</th>\n" +
    "					<th>Description</th>\n" +
    "				</tr>\n" +
    "			</thead>\n" +
    "			\n" +
    "			<tbody>    	      \n" +
    "				<tr id=\"message\">\n" +
    "					<td>\n" +
    "						count\n" +
    "					</td>\n" +
    "				<td>\n" +
    "					Number of deleted messages.							\n" +
    "				</td>\n" +
    "				</tr>\n" +
    "				\n" +
    "			</tbody>\n" +
    "			</table>\n" +
    "			\n" +
    "			<div class=\"alert alert-info\">\n" +
    "				<p>\n" +
    "				<h4 class=\"alert-heading\">Usage </h4>\n" +
    "				</p> <br/>				\n" +
    "				 <div class=\"gridStyle\" ng-grid=\"messageGrid\">\n" +
    "				</div>	\n" +
    "			</div>\n" +
    "			\n" +
    "			<div class=\"container-fluid\">\n" +
    "				<div class=\"row\">\n" +
    "					<div class=\"col-md-5\">\n" +
    "						<div class=\"panel panel-primary\">\n" +
    "							<div class=\"panel-heading\">\n" +
    "								<span class=\"glyphicon glyphicon-comment\"></span> Messaging\n" +
    "							</div>\n" +
    "							<div class=\"panel-body\">\n" +
    "								<ul class=\"chat\">\n" +
    "								\n" +
    "									<li ng-repeat=\"msg in currentConversation.messages|orderBy:'id'\" class=\"{{ getListAlignment(msg) }}\" scrollglue>\n" +
    "									\n" +
    "										<span ng-if=\"msg.sender.toUpperCase() == 'ME'\" class=\"chat-img pull-left\">\n" +
    "											<img src=\"assets/images/ME.gif\" alt=\"User Avatar\" class=\"img-circle\">\n" +
    "										</span>\n" +
    "										\n" +
    "										<span ng-if=\"msg.sender.toUpperCase() != 'ME'\" class=\"chat-img pull-right\">\n" +
    "											<img src=\"assets/images/U.gif\" alt=\"User Avatar\" class=\"img-circle\">\n" +
    "										</span>\n" +
    "									\n" +
    "										<div class=\"chat-body clearfix\" ng-if=\"msg.sender.toUpperCase() == 'ME'\" id=\"chatMsg_{{$index}}\">\n" +
    "										\n" +
    "											<div class=\"header\">\n" +
    "												<strong class=\"primary-font\">{{msg.sender}}</strong> <small class=\"pull-right text-muted\">\n" +
    "													<span class=\"glyphicon glyphicon-time\"></span>{{msg.date}}</small>\n" +
    "											</div>\n" +
    "											<p>\n" +
    "												{{msg.message}}\n" +
    "											</p>\n" +
    "										</div>\n" +
    "										\n" +
    "										<div class=\"chat-body clearfix\" ng-if=\"msg.sender.toUpperCase() != 'ME'\" id=\"chatMsg_{{$index}}\">\n" +
    "										\n" +
    "											<div class=\"header\">\n" +
    "												<small class=\" text-muted\"><span class=\"glyphicon glyphicon-time\"></span>{{msg.date}}</small>\n" +
    "												<strong class=\"pull-right primary-font\">{{msg.sender}}</strong>\n" +
    "											</div>\n" +
    "											<p>\n" +
    "												{{msg.message}}\n" +
    "											</p>\n" +
    "										</div>\n" +
    "										\n" +
    "									</li>							\n" +
    "									\n" +
    "								</ul>\n" +
    "							</div>\n" +
    "							<div class=\"panel-footer\">\n" +
    "								<div class=\"input-group\">\n" +
    "									<input ng-model=\"btnMessage\" name=\"btnMessage\" required type=\"text\" class=\"form-control input-sm\" placeholder=\"Type your message here...\" ng-enter=\"onSendMessage()\"/>\n" +
    "									<span class=\"input-group-btn\">\n" +
    "										<button class=\"btn btn-primary btn-sm\" id=\"btnChat\" ng-click=\"onSendMessage()\">&nbsp;&nbsp;&nbsp;Send&nbsp;&nbsp;&nbsp;</button>\n" +
    "										<!--<button class=\"btn btn-success btn-sm\" id=\"btnRefresh\" ng-click=\"onRefreshConveration()\">Refresh</button>-->\n" +
    "									</span>									 								\n" +
    "								</div>\n" +
    "							</div>\n" +
    "							<a id=\"bottom\"></a>	\n" +
    "						</div>\n" +
    "					</div>\n" +
    "				</div>\n" +
    "			</div>\n" +
    "		\n" +
    "		</div>\n" +
    "	</div>\n" +
    "	\n" +
    "	\n" +
    "</div>\n" +
    "					\n" +
    "	\n" +
    "		\n" +
    "		\n" +
    "	\n" +
    "");
}]);

angular.module("mms/mms.tpl.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("mms/mms.tpl.html",
    "<div class=\"container-fluid\">\n" +
    "	<div class=\"row\">\n" +
    "		<div class=\"col-md-3\" id=\"sidebar\" ng-include=\"'assets/view/services_sidebar.html'\"></div>\n" +
    "\n" +
    "		<div class=\"col-md-9\" id=\"content\">\n" +
    "		\n" +
    "			<div class=\"alert alert-info\">\n" +
    "				<h3 class=\"alert-heading\">MMS API</h3>\n" +
    "			</div>\n" +
    "			\n" +
    "			\n" +
    "			<div class=\"alert alert-success\">\n" +
    "				<h4 class=\"alert-heading\"> <button class=\"btn btn-default btn-xs\">POST</button> Configuration Parameters </h4> \n" +
    "				<p class=\"long-wrap\">\n" +
    "				{{parameterLink}}\n" +
    "				</p>\n" +
    "				\n" +
    "				<br/><br/>\n" +
    "				\n" +
    "				<form class=\"form-horizontal\" role=\"form\" ng-submit=\"submitMmsForm()\">\n" +
    "				  <div class=\"form-group\">\n" +
    "					<label for=\"preferences_apn_mmsc\" class=\"col-sm-4 control-label\">MMSC URL</label>\n" +
    "					<div class=\"col-sm-6\">\n" +
    "						<input type=\"text\" class=\"form-control\" ng-model=\"mmsForm.preferences_apn_mmsc\" required=\"true\" id=\"preferences_apn_mmsc\" placeholder=\"MMSC URL\">\n" +
    "					</div>\n" +
    "				  </div>\n" +
    "				  <div class=\"form-group\">\n" +
    "					<label for=\"preferences_apn_mms_proxy\" class=\"col-sm-4 control-label\">MMSC Proxy Host</label>\n" +
    "					<div class=\"col-sm-6\">\n" +
    "					  <input type=\"text\" class=\"form-control\" ng-model=\"mmsForm.preferences_apn_mms_proxy\" required=\"true\" id=\"preferences_apn_mms_proxy\" placeholder=\"MMS Proxy Host\">\n" +
    "					</div>\n" +
    "				  </div>\n" +
    "				  <div class=\"form-group\">\n" +
    "					<label for=\"preferences_apn_mms_port\" class=\"col-sm-4 control-label\">MMSC Proxy Port</label>\n" +
    "					<div class=\"col-sm-3\">\n" +
    "					  <input type=\"number\" class=\"form-control\" ng-model=\"mmsForm.preferences_apn_mms_port\" id=\"preferences_apn_mms_port\" ng-minlength=\"0\" ng-maxlength=\"9999\" min=\"0\" max=\"9999\" placeholder=\"MMSC Proxy Port\">\n" +
    "					</div>\n" +
    "				  </div>\n" +
    "				\n" +
    "				  <div class=\"form-group\">\n" +
    "					<label for=\"preferences_apn_mms_user\" class=\"col-sm-4 control-label\">MMSC User</label>\n" +
    "					<div class=\"col-sm-6\">\n" +
    "					  <input type=\"text\" class=\"form-control\" ng-model=\"mmsForm.preferences_apn_mms_user\" id=\"preferences_apn_mms_user\" placeholder=\"MMSC User\">\n" +
    "					</div>\n" +
    "				  </div>\n" +
    "				  \n" +
    "				  \n" +
    "				  <div class=\"form-group\">\n" +
    "					<label for=\"preferences_apn_mms_password\" class=\"col-sm-4 control-label\">MMSC Password</label>\n" +
    "					<div class=\"col-sm-6\">\n" +
    "					  <input type=\"text\" class=\"form-control\" ng-model=\"mmsForm.preferences_apn_mms_password\" id=\"preferences_apn_mms_password\" placeholder=\"MMSC Password\">\n" +
    "					</div>\n" +
    "				  </div>\n" +
    "				  \n" +
    "				  <div class=\"form-group\">\n" +
    "					<label for=\"preferences_apn_mms_user_agent\" class=\"col-sm-4 control-label\">MMSC User Agent</label>\n" +
    "					<div class=\"col-sm-6\">\n" +
    "					  <input type=\"text\" class=\"form-control\" ng-model=\"mmsForm.preferences_apn_mms_user_agent\" required=\"true\" id=\"preferences_apn_mms_user_agent\" placeholder=\"MMSC User Agent\">\n" +
    "					</div>\n" +
    "				  </div>\n" +
    "				\n" +
    "				  <div class=\"form-group\">\n" +
    "					<div class=\"col-sm-offset-4 col-sm-10\">\n" +
    "					  <button type=\"submit\" class=\"btn btn-default\" ng-disabled=\"mmsFormStatus\">Save</button>\n" +
    "					</div>\n" +
    "				  </div>\n" +
    "				</form>\n" +
    "				\n" +
    "				<br/>\n" +
    "				{{ saveStatus}}\n" +
    "			</div>\n" +
    "			\n" +
    "			\n" +
    "			<table id=\"sms_api_doc\" class=\"table table-striped\">\n" +
    "				<thead>\n" +
    "					<tr>\n" +
    "						<th>Parameter</th>\n" +
    "						<th>Description</th>\n" +
    "					</tr>\n" +
    "				</thead>\n" +
    "				\n" +
    "				<tbody>    	      \n" +
    "					<tr id=\"preferences_apn_mmsc\">\n" +
    "						<td>\n" +
    "							preferences_apn_mmsc\n" +
    "						</td>\n" +
    "					<td>\n" +
    "						Multimedia Messaging Service Provider (MMSC) URL.\n" +
    "					</td>\n" +
    "					</tr>\n" +
    "					\n" +
    "					<tr id=\"preferences_apn_mms_proxy\">\n" +
    "						<td>\n" +
    "							preferences_apn_mms_proxy\n" +
    "						</td>\n" +
    "						<td>\n" +
    "							MMSC proxy.\n" +
    "						</td>\n" +
    "					</tr>\n" +
    "					\n" +
    "					<tr id=\"preferences_apn_mms_port\">\n" +
    "						<td>\n" +
    "							preferences_apn_mms_port\n" +
    "						</td>\n" +
    "						<td>\n" +
    "							MMSC proxy port.\n" +
    "						</td>\n" +
    "					</tr>\n" +
    "\n" +
    "					<tr id=\"preferences_apn_mms_user\">\n" +
    "						<td>\n" +
    "							preferences_apn_mms_user\n" +
    "						</td>\n" +
    "						<td>\n" +
    "							MMSC user name.\n" +
    "						</td>\n" +
    "					</tr>\n" +
    "\n" +
    "					<tr id=\"preferences_apn_mms_password\">\n" +
    "						<td>\n" +
    "							preferences_apn_mms_password\n" +
    "						</td>\n" +
    "						<td>\n" +
    "							MMSC user password.\n" +
    "						</td>\n" +
    "					</tr>\n" +
    "\n" +
    "					<tr id=\"preferences_apn_mms_user_agent\">\n" +
    "						<td>\n" +
    "							preferences_apn_mms_user_agent\n" +
    "						</td>\n" +
    "						<td>\n" +
    "							MMSC user agent\n" +
    "						</td>\n" +
    "					</tr>\n" +
    "					\n" +
    "				</tbody>\n" +
    "			</table>\n" +
    "			\n" +
    "			<div class=\"alert alert-warning\">\n" +
    "				<h4 class=\"alert-heading\">Response</h4>\n" +
    "				<p class=\"long-wrap\">\n" +
    "				<code>\n" +
    "				{\n" +
    "					\"description\": \"\",\n" +
    "					\"requestMethod\": \"POST\",\n" +
    "					\"isSuccessful\": true\n" +
    "				}\n" +
    "				</code>\n" +
    "				</p>\n" +
    "			</div>\n" +
    "			\n" +
    "			<table id=\"sms_api_doc\" class=\"table table-striped\">\n" +
    "			<thead>\n" +
    "				<tr>\n" +
    "					<th>Field</th>\n" +
    "					<th>Description</th>\n" +
    "				</tr>\n" +
    "			</thead>\n" +
    "			\n" +
    "			<tbody>    	      \n" +
    "				<tr id=\"description\">\n" +
    "					<td>\n" +
    "						description\n" +
    "					</td>\n" +
    "				<td>\n" +
    "					Contains the error message if isSuccessful is \"false\".\n" +
    "				</td>\n" +
    "				</tr>\n" +
    "				\n" +
    "				<tr id=\"requestMethod\">\n" +
    "					<td>\n" +
    "						requestMethod\n" +
    "					</td>\n" +
    "				<td>\n" +
    "					Request method. Default to \"GET\", \"POST\" or \"DELETE\" depending on the HTTP method.\n" +
    "				</td>\n" +
    "				</tr>\n" +
    "				\n" +
    "				<tr id=\"requestMethod\">\n" +
    "					<td>\n" +
    "						isSuccessful\n" +
    "					</td>\n" +
    "				<td>\n" +
    "					true if operation is successful. Otherwise returns false.\n" +
    "				</td>\n" +
    "				</tr>\n" +
    "				\n" +
    "				\n" +
    "			</tbody>\n" +
    "			</table>\n" +
    "			\n" +
    "			\n" +
    "			<br/><br/>\n" +
    "			\n" +
    "			\n" +
    "			\n" +
    "\n" +
    "\n" +
    "			<div class=\"alert alert-success\">\n" +
    "				<h4 class=\"alert-heading\"> <button class=\"btn btn-default btn-xs\">POST</button> Send MMS </h4> \n" +
    "				<p></p> <br>\n" +
    "				<p class=\"long-wrap\">\n" +
    "				{{mmsLink}}\n" +
    "				</p>\n" +
    "			</div>\n" +
    "			\n" +
    "			<table id=\"sms_api_doc\" class=\"table table-striped\">\n" +
    "				<thead>\n" +
    "					<tr>\n" +
    "						<th>Parameter</th>\n" +
    "						<th>Description</th>\n" +
    "					</tr>\n" +
    "				</thead>\n" +
    "				\n" +
    "				<tbody>    	      \n" +
    "					<tr id=\"To\">\n" +
    "						<td>\n" +
    "							To\n" +
    "						</td>\n" +
    "						<td>\n" +
    "						The destination number or contact name. Mandatory parameter.\n" +
    "						</td>\n" +
    "					</tr>\n" +
    "\n" +
    "					<tr id=\"CC\">\n" +
    "						<td>\n" +
    "							CC\n" +
    "						</td>\n" +
    "						<td>\n" +
    "						CC destination number or contact name.\n" +
    "						</td>\n" +
    "					</tr>\n" +
    "\n" +
    "					<tr id=\"BCC\">\n" +
    "						<td>\n" +
    "							BCC\n" +
    "						</td>\n" +
    "						<td>\n" +
    "						BCC destination number or contact name.\n" +
    "						</td>\n" +
    "					</tr>\n" +
    "					\n" +
    "					<tr id=\"Subject\">\n" +
    "						<td>\n" +
    "							Subject\n" +
    "						</td>\n" +
    "						<td>\n" +
    "							MMS message subject.\n" +
    "						</td>\n" +
    "					</tr>\n" +
    "\n" +
    "					<tr id=\"Body\">\n" +
    "						<td>\n" +
    "							Body\n" +
    "						</td>\n" +
    "						<td>\n" +
    "							MMS message body.\n" +
    "						</td>\n" +
    "					</tr>\n" +
    "\n" +
    "					<tr id=\"DeliveryReport\">\n" +
    "						<td>\n" +
    "							DeliveryReport\n" +
    "						</td>\n" +
    "						<td>\n" +
    "							0 for no delivery report, any other values indicate delivery report is required. Default to 0 for no delivery report.\n" +
    "						</td>\n" +
    "					</tr>\n" +
    "\n" +
    "					<tr id=\"ReadReport\">\n" +
    "						<td>\n" +
    "							ReadReport\n" +
    "						</td>\n" +
    "						<td>\n" +
    "							0 for no read report, any other values indicate read report is required. Default to 0 for no read report.\n" +
    "						</td>\n" +
    "					</tr>\n" +
    "					\n" +
    "					<tr id=\"PartData_{number}\">\n" +
    "						<td>\n" +
    "							PartData_{number}\n" +
    "						</td>\n" +
    "						<td>\n" +
    "							MMS media data to be uploaded. The number starts from 0 and maximum is 30. Each part data must have a corresponding part content type with the same number.\n" +
    "						</td>\n" +
    "					</tr>\n" +
    "\n" +
    "					<tr id=\"PartContentType_{number}\">\n" +
    "						<td>\n" +
    "							PartContentType_{number}\n" +
    "						</td>\n" +
    "						<td>\n" +
    "							MMS media content type. Must be one of the <a href=\"#/mms#contentapi\">supported content types</a>. The number starts from 0 and maximum is 30.\n" +
    "						</td>\n" +
    "					</tr>\n" +
    "					\n" +
    "				</tbody>\n" +
    "			</table>\n" +
    "	\n" +
    "			\n" +
    "			<div class=\"alert alert-warning\">\n" +
    "				<h4 class=\"alert-heading\">Response</h4>\n" +
    "				<p class=\"long-wrap\">\n" +
    "				<code>			\n" +
    "\n" +
    "					{\n" +
    "					    \"message\": {\n" +
    "					        \"bcc\": \"\",\n" +
    "					        \"body\": \"msg body\",\n" +
    "					</code><br/><code>\n" +
    "					        \"cc\": \"\",\n" +
    "					        \"date\": \"2016-03-20T21:59:45GMT+08:00\",\n" +
    "					</code><br/><code>\n" +
    "					        \"to\": \"8632323232323\",\n" +
    "					        \"subject\": \"test api mms\",\n" +
    "					        \"id\": \"6492044552370211736\",\n" +
    "					</code><br/><code>\n" +
    "					        \"isDelivered\": false,\n" +
    "					        \"isRead\": false,\n" +
    "					        \"readReport\": false,\n" +
    "					</code><br/><code>\n" +
    "					        \"deliveryReport\": false\n" +
    "					    },\n" +
    "					    \"description\": \"\",\n" +
    "					    \"requestMethod\": \"POST\",\n" +
    "					</code><br/><code>\n" +
    "					    \"isSuccessful\": true\n" +
    "					}\n" +
    "				</code>\n" +
    "				</p>\n" +
    "			</div>\n" +
    "			\n" +
    "			\n" +
    "			<table id=\"mms_api_doc\" class=\"table table-striped\">\n" +
    "			<thead>\n" +
    "				<tr>\n" +
    "					<th>Field</th>\n" +
    "					<th>Description</th>\n" +
    "				</tr>\n" +
    "			</thead>\n" +
    "			\n" +
    "			<tbody>    	    \n" +
    "\n" +
    "				<tr id=\"message\">\n" +
    "					<td>\n" +
    "						message\n" +
    "					</td>\n" +
    "					\n" +
    "				<td>\n" +
    "					\n" +
    "					Message object containing the following fields for the sent message.\n" +
    "					<br/><br/>\n" +
    "					\n" +
    "					<dl class=\"dl-horizontal\">\n" +
    "					  <dt>to</dt>\n" +
    "					  <dd>To number.</dd>\n" +
    "					  <dt>cc</dt>\n" +
    "					  <dd>CC number.</dd>\n" +
    "					  <dt>bcc</dt>\n" +
    "					  <dd>BCC number</dd>\n" +
    "					  <dt>id</dt>\n" +
    "					  <dd>A <b>unique identifier</b> that can be used to check the message status.</dd>\n" +
    "					  <dt>subject</dt>\n" +
    "					  <dd>Message subject</dd>		\n" +
    "					  <dt>body</dt>\n" +
    "					  <dd>Message body</dd>			\n" +
    "					  <dt>deliveryReport</dt>\n" +
    "					  <dd>Flag to indicates if delivery report is required.</dd>	\n" +
    "					  <dt>readReport</dt>\n" +
    "					  <dd>Flag to indicates if read report is required.</dd>	\n" +
    "					  <dt>isDelivered</dt>\n" +
    "					  <dd>Flag to indicates if message is delivered.</dd>	\n" +
    "					  <dt>isRead</dt>\n" +
    "					  <dd>Flag to indicates if message is read.</dd>	\n" +
    "					  <dt>date</dt>\n" +
    "					  <dd>Message sent date.</dd>				  \n" +
    "					</dl>				\n" +
    "\n" +
    "\n" +
    "				</td>\n" +
    "				</tr>\n" +
    "			</tbody>\n" +
    "			</table>			\n" +
    "\n" +
    "\n" +
    "\n" +
    "			<div class=\"alert alert-success\">\n" +
    "				<h4 id=\"contentapi\" class=\"alert-heading\"> <button class=\"btn btn-default btn-xs\">Get</button> \n" +
    "				<a name=\"contentapi\"></a>\n" +
    "				Check MMS Status\n" +
    "				</h4> \n" +
    "				<p></p> <br>\n" +
    "				<p class=\"long-wrap\">\n" +
    "				{{mmsLink}}<b>{unique identifier}</b>\n" +
    "				</p>\n" +
    "			</div>\n" +
    "			\n" +
    "			<div class=\"alert alert-warning\">\n" +
    "				<h4 class=\"alert-heading\">Response</h4>\n" +
    "				<p class=\"long-wrap\">\n" +
    "				<code>			\n" +
    "					{\n" +
    "					    \"message\": {\n" +
    "					        \"bcc\": \"\",\n" +
    "					        \"body\": \"msg body\",\n" +
    "					</code><br/><code>\n" +
    "					        \"cc\": \"\",\n" +
    "					        \"date\": \"2016-03-20T22:03:13GMT+08:00\",\n" +
    "					        \"to\": \"8632323232323\",\n" +
    "					        \"subject\": \"test api mms\",\n" +
    "					 	</code><br/><code>\n" +
    "					        \"id\": \"7131739047516455185\",\n" +
    "					        \"isDelivered\": true,\n" +
    "					        \"isRead\": false,\n" +
    "						</code><br/><code>\n" +
    "					        \"readReport\": false,\n" +
    "					        \"deliveryReport\": false\n" +
    "					    },\n" +
    "					   	</code><br/><code>\n" +
    "					    \"description\": \"\",\n" +
    "					    \"requestMethod\": \"GET\",\n" +
    "					    \"isSuccessful\": true\n" +
    "					}\n" +
    "				</code>\n" +
    "				</p>\n" +
    "			</div>\n" +
    "			\n" +
    "			<table id=\"sms_api_doc\" class=\"table table-striped\">\n" +
    "			<thead>\n" +
    "				<tr>\n" +
    "					<th>Field</th>\n" +
    "					<th>Description</th>\n" +
    "				</tr>\n" +
    "			</thead>\n" +
    "			\n" +
    "			<tbody>    	    \n" +
    "\n" +
    "				<tr id=\"message\">\n" +
    "					<td>\n" +
    "						message\n" +
    "					</td>\n" +
    "					\n" +
    "				<td>\n" +
    "						Message object containing the following fields for the sent message.\n" +
    "					<br/><br/>\n" +
    "					\n" +
    "					<dl class=\"dl-horizontal\">\n" +
    "					  <dt>to</dt>\n" +
    "					  <dd>To number.</dd>\n" +
    "					  <dt>cc</dt>\n" +
    "					  <dd>CC number.</dd>\n" +
    "					  <dt>bcc</dt>\n" +
    "					  <dd>BCC number</dd>\n" +
    "					  <dt>id</dt>\n" +
    "					  <dd>A <b>unique identifier</b> that can be used to check the message status.</dd>\n" +
    "					  <dt>subject</dt>\n" +
    "					  <dd>Message subject</dd>		\n" +
    "					  <dt>body</dt>\n" +
    "					  <dd>Message body</dd>			\n" +
    "					  <dt>deliveryReport</dt>\n" +
    "					  <dd>Flag to indicates if delivery report is required.</dd>	\n" +
    "					  <dt>readReport</dt>\n" +
    "					  <dd>Flag to indicates if read report is required.</dd>	\n" +
    "					  <dt>isDelivered</dt>\n" +
    "					  <dd>Flag to indicates if message is delivered.</dd>	\n" +
    "					  <dt>isRead</dt>\n" +
    "					  <dd>Flag to indicates if message is read.</dd>	\n" +
    "					  <dt>date</dt>\n" +
    "					  <dd>Message sent date.</dd>				  \n" +
    "					</dl>				\n" +
    "				</td>\n" +
    "				</tr>\n" +
    "				\n" +
    "			</tbody>\n" +
    "			</table>			\n" +
    "\n" +
    "			<div class=\"alert alert-success\">\n" +
    "				<h4 id=\"contentapi\" class=\"alert-heading\"> <button class=\"btn btn-default btn-xs\">Get</button> \n" +
    "				<a name=\"contentapi\"></a>\n" +
    "				List Supported MMS Content Types \n" +
    "				</h4> \n" +
    "				<p></p> <br>\n" +
    "				<p class=\"long-wrap\">\n" +
    "				<a ng-href=\"{{mmsLink}}supportedcontenttypes\">{{mmsLink}}supportedcontenttypes</a>\n" +
    "				</p>\n" +
    "			</div>\n" +
    "			\n" +
    "			<div class=\"alert alert-warning\">\n" +
    "				<h4 class=\"alert-heading\">Response</h4>\n" +
    "				<p class=\"long-wrap\">\n" +
    "				<code>\n" +
    "					{\"supportedContentTypes\":[\"text/plain\",\"text/html\",\"text/x-vCalendar\",\"text/x-vCard\",\n" +
    "				</code><br/><code>\n" +
    "					\"image/jpeg\",\"image/gif\",\"image/vnd.wap.wbmp\",\"image/png\",\"image/jpg\",\"image/x-ms-bmp\",\n" +
    "				</code><br/><code>\n" +
    "					\"audio/aac\",\"audio/amr\",\"audio/imelody\",\"audio/mid\",\"audio/midi\",\"audio/mp3\",\"audio/mp4\",\n" +
    "				</code><br/><code>\n" +
    "					\"audio/mpeg3\",\"audio/mpeg\",\"audio/mpg\",\"audio/x-mid\",\"audio/x-midi\",\"audio/x-mp3\",\n" +
    "				</code><br/><code>\n" +
    "					\"audio/x-mpeg3\",\"audio/x-mpeg\",\"audio/x-mpg\",\"audio/x-wav\",\"audio/3gpp\",\"application/ogg\",\n" +
    "				</code><br/><code>\n" +
    "					\"video/3gpp\",\"video/3gpp2\",\"video/h263\",\"video/mp4\",\"application/smil\",\n" +
    "				</code><br/><code>\n" +
    "					\"application/vnd.wap.xhtml+xml\",\"application/xhtml+xml\",\"application/vnd.oma.drm.content\",\n" +
    "				</code><br/><code>\n" +
    "					\"application/vnd.oma.drm.message\",\"text/texmacs\",\"image/x-ms-bmp\",\"image/bmp\",\n" +
    "				</code><br/><code>\n" +
    "					\"audio/x-wav\",\"audio/amr-wb\",\"audio/x-ms-wma\",\"audio/vorbis\",\"video/mp2ts\"],\n" +
    "				</code><br/><code>\n" +
    "					\"description\":\"\",\"requestMethod\":\"GET\",\"isSuccessful\":true}\n" +
    "				</code>\n" +
    "				</p>\n" +
    "			</div>\n" +
    "			\n" +
    "			<table id=\"sms_api_doc\" class=\"table table-striped\">\n" +
    "			<thead>\n" +
    "				<tr>\n" +
    "					<th>Field</th>\n" +
    "					<th>Description</th>\n" +
    "				</tr>\n" +
    "			</thead>\n" +
    "			\n" +
    "			<tbody>    	    \n" +
    "\n" +
    "				<tr id=\"supportedcontenttypes\">\n" +
    "					<td>\n" +
    "						supportedcontenttypes\n" +
    "					</td>\n" +
    "					\n" +
    "				<td>\n" +
    "					A list of supported MMS content types, e.g. \"text/plain\", \"text/x-vCalendar\",\"text/x-vCard\",\n" +
    "					\"video/mp4\".\n" +
    "				</td>\n" +
    "				</tr>\n" +
    "				\n" +
    "			</tbody>\n" +
    "			</table>			\n" +
    "		\n" +
    "			\n" +
    "			\n" +
    "		\n" +
    "			<div class=\"alert alert-info row\">\n" +
    "				<p>\n" +
    "				<h4 class=\"alert-heading\">Usage (using <a href=\"http://uncorkedstudios.com/blog/multipartformdata-file-upload-with-angularjs\">FormData object </a>)</h4>\n" +
    "				</p> <br/>	\n" +
    "					\n" +
    "				<form class=\"form-horizontal\" ng-submit=\"sendMmsMessage()\">\n" +
    "				\n" +
    "				  <div class=\"form-group\">\n" +
    "					<label for=\"to\" class=\"col-sm-2 control-label\">To</label>\n" +
    "					<div class=\"col-sm-10\">\n" +
    "						<input type=\"text\" class=\"form-control\" ng-model=\"mmsMessage.to\" required=\"true\" id=\"to\" placeholder=\"To\"/>\n" +
    "					</div>\n" +
    "				  </div>\n" +
    "				  \n" +
    "				  <div class=\"form-group\">\n" +
    "					<label for=\"subject\" class=\"col-sm-2 control-label\">Subject</label>\n" +
    "					<div class=\"col-sm-10\">\n" +
    "	 			  		<input type=\"text\" class=\"form-control\" ng-model=\"mmsMessage.subject\" required=\"true\" id=\"subject\" placeholder=\"Subject\"/>\n" +
    "	 			  	</div>\n" +
    "				  </div>\n" +
    "\n" +
    "				  <div class=\"form-group\">\n" +
    "					<label for=\"text\" class=\"col-sm-2 control-label\">Body</label>	\n" +
    "					<div class=\"col-sm-10\">			\n" +
    "						<input type=\"text\" class=\"form-control\" ng-model=\"mmsMessage.body\" required=\"true\" id=\"text\" placeholder=\"Text\" />\n" +
    "					</div>\n" +
    "				  </div>\n" +
    "				\n" +
    "\n" +
    "				<div class=\"form-inline\">\n" +
    "					<div class=\"form-group col-sm-2\">\n" +
    "					    &nbsp;\n" +
    "					</div>\n" +
    "					<div class=\"form-group\">\n" +
    "					    <label for=\"media\" class=\"col-sm-2 control-label\">Media</label>\n" +
    "					    <input type=\"file\" file-model=\"mmsMessage.partdata_1\"/>	\n" +
    "					</div>\n" +
    "					 <div class=\"form-group\">\n" +
    "					    <label for=\"mediaType\" class=\"col-sm-2 control-label\">Type</label>\n" +
    "					   	<select class=\"form-control\" ng-required='mmsMessage.partcontenttype_1' ng-model=\"mmsMessage.partcontenttype_1\" ng-options=\"s as s for s in mimeTypes\"></select> 	\n" +
    "					</div>\n" +
    "\n" +
    "				</div>\n" +
    "\n" +
    "				<div class=\"form-inline\">\n" +
    "					<div class=\"form-group col-sm-2\">\n" +
    "					    &nbsp;\n" +
    "					</div>\n" +
    "					<div class=\"form-group\">\n" +
    "					    <label for=\"media\" class=\"col-sm-2 control-label\">Media</label>\n" +
    "					    <input type=\"file\" file-model=\"mmsMessage.partdata_2\"/>	\n" +
    "					</div>\n" +
    "					 <div class=\"form-group\">\n" +
    "					    <label for=\"mediaType\" class=\"col-sm-2 control-label\">Type</label>\n" +
    "					   	<select class=\"form-control\" ng-required='mmsMessage.partcontenttype_2' ng-model=\"mmsMessage.partcontenttype_2\" ng-options=\"s as s for s in mimeTypes\"></select> 	\n" +
    "					</div>\n" +
    "\n" +
    "				</div>\n" +
    "\n" +
    "					<div class=\"form-inline\">\n" +
    "					<div class=\"form-group col-sm-2\">\n" +
    "					    &nbsp;\n" +
    "					</div>\n" +
    "					<div class=\"form-group\">\n" +
    "					    <label for=\"media\" class=\"col-sm-2 control-label\">Media</label>\n" +
    "					    <input type=\"file\" file-model=\"mmsMessage.partdata_3\"/>	\n" +
    "					</div>\n" +
    "					 <div class=\"form-group\">\n" +
    "					    <label for=\"mediaType\" class=\"col-sm-2 control-label\">Type</label>\n" +
    "					   	<select class=\"form-control\" ng-required='mmsMessage.partcontenttype_3' ng-model=\"mmsMessage.partcontenttype_3\" ng-options=\"s as s for s in mimeTypes\"></select> 	\n" +
    "					</div>\n" +
    "\n" +
    "				</div>\n" +
    "			\n" +
    "				 <div class=\"form-group\">\n" +
    "				  	<div class=\"col-sm-offset-2 col-sm-10\">\n" +
    "				  		<br/>\n" +
    "					  <button type=\"submit\" class=\"btn btn-default\" ng-disabled=\"mmsMessageStatus\">Send MMS</button>\n" +
    "\n" +
    "					  <br/>\n" +
    "					  <br/>\n" +
    "					  {{mmsSendStatus}}\n" +
    "					</div>\n" +
    "				  </div>						\n" +
    "				</form>\n" +
    "\n" +
    "			</div>	\n" +
    "		</div>\n" +
    "		\n" +
    "		</div>\n" +
    "	</div>\n" +
    "	\n" +
    "	\n" +
    "</div>\n" +
    "					\n" +
    "	\n" +
    "		\n" +
    "		\n" +
    "	\n" +
    "");
}]);

angular.module("notif/notif.tpl.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("notif/notif.tpl.html",
    "<div class=\"container-fluid\">\n" +
    "	<div class=\"alert alert-info\">\n" +
    "		<h3 class=\"alert-heading\">\n" +
    "			<i class=\"fa fa-comment-o\"></i>  Notifications\n" +
    "		</h3>\n" +
    "		\n" +
    "		<br/>\n" +
    "		<div class=\"btn-group\">\n" +
    "			<button type=\"button\" class=\"btn btn-warning\" ng-click=\"purge()\">Purge</button>\n" +
    "		</div>\n" +
    "				\n" +
    "			</p>\n" +
    "			<br />\n" +
    "			<div class=\"gridStyle\" ng-grid=\"notificationGrid\"></div>\n" +
    "	</div>\n" +
    "</div>");
}]);

angular.module("sensor/sensor.tpl.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("sensor/sensor.tpl.html",
    "<div class=\"container-fluid\">\n" +
    "	<div class=\"row\">\n" +
    "		<div class=\"col-md-3\" id=\"sidebar\" ng-include=\"'assets/view/services_sidebar.html'\"></div>\n" +
    "		\n" +
    "		\n" +
    "		<div class=\"col-md-9\" id=\"content\">\n" +
    "			\n" +
    "			<div class=\"alert alert-info\">\n" +
    "				<h3 class=\"alert-heading\">Sensor API</h3>\n" +
    "			</div>\n" +
    "						\n" +
    "			<br/>\n" +
    "			\n" +
    "			<div class=\"alert alert-success\">				\n" +
    "				<h4 class=\"alert-heading\"> <button class=\"btn btn-default btn-xs\">Get</button> Magnetic Field </h4>\n" +
    "				<br/>\n" +
    "				<p class=\"long-wrap\">\n" +
    "				<a href=\"{{sensorLink}}magnetic\">{{sensorLink}}magnetic</a>\n" +
    "				</p>\n" +
    "			</div>	\n" +
    "			\n" +
    "			<div class=\"alert alert-warning\">\n" +
    "				<h4 class=\"alert-heading\">Response</h4>\n" +
    "				<p class=\"long-wrap\">\n" +
    "				<code>\n" +
    "				{\"x\":12.48,\"y\":40.559998,\"z\":9.66,\n" +
    "				</code>\n" +
    "				<br/>\n" +
    "				<code>\n" +
    "				\"timestamp\":\"2016-09-24T22:34:21GMT+08:00\",\n" +
    "				</code>\n" +
    "				<br/>\n" +
    "				<code>\n" +
    "				\"description\":\"\",\"requestMethod\":\"GET\",\"isSuccessful\":true\n" +
    "				}\n" +
    "				</code>\n" +
    "				</p>\n" +
    "			</div>\n" +
    "\n" +
    "			<table id=\"sms_api_doc\" class=\"table table-striped\">\n" +
    "			<thead>\n" +
    "				<tr>\n" +
    "					<th>Field</th>\n" +
    "					<th>Description</th>\n" +
    "				</tr>\n" +
    "			</thead>\n" +
    "			\n" +
    "			<tbody>   \n" +
    "				<tr>\n" +
    "					<td>\n" +
    "						x\n" +
    "					</td>\n" +
    "				<td>\n" +
    "					X magnetic field.\n" +
    "				</td>\n" +
    "				</tr>\n" +
    "				<tr>\n" +
    "					<td>\n" +
    "						y\n" +
    "					</td>\n" +
    "				<td>\n" +
    "					Y magnetic field.\n" +
    "				</td>\n" +
    "				</tr>\n" +
    "				<tr>\n" +
    "					<td>\n" +
    "						z\n" +
    "					</td>\n" +
    "				<td>\n" +
    "					Z magnetic field.\n" +
    "				</td>\n" +
    "				</tr>\n" +
    "				<tr>\n" +
    "					<td>\n" +
    "						timestamp\n" +
    "					</td>\n" +
    "				<td>\n" +
    "					Timestamp the sensor data is captured.\n" +
    "				</td>\n" +
    "				</tr> 	      \n" +
    "				<tr id=\"description\">\n" +
    "					<td>\n" +
    "						description\n" +
    "					</td>\n" +
    "				<td>\n" +
    "					Contains the error message if isSuccessful is \"false\".\n" +
    "				</td>\n" +
    "				</tr>\n" +
    "				\n" +
    "				<tr id=\"requestMethod\">\n" +
    "					<td>\n" +
    "						requestMethod\n" +
    "					</td>\n" +
    "				<td>\n" +
    "					Request method. Default to \"GET\", \"POST\" or \"DELETE\" depending on the HTTP method.\n" +
    "				</td>\n" +
    "				</tr>\n" +
    "				\n" +
    "				<tr id=\"isSuccessful\">\n" +
    "					<td>\n" +
    "						isSuccessful\n" +
    "					</td>\n" +
    "				<td>\n" +
    "					true if operation is successful. Otherwise returns false.\n" +
    "				</td>\n" +
    "				</tr>\n" +
    "				\n" +
    "				\n" +
    "			</tbody>\n" +
    "			</table>\n" +
    "\n" +
    "			<br/><br/>\n" +
    "\n" +
    "			<div class=\"alert alert-success\">				\n" +
    "				<h4 class=\"alert-heading\"> <button class=\"btn btn-default btn-xs\">Get</button> Accelerometer </h4>\n" +
    "				<br/>\n" +
    "				<p class=\"long-wrap\">\n" +
    "				<a href=\"{{sensorLink}}accelerometer\">{{sensorLink}}accelerometer</a>\n" +
    "				</p>\n" +
    "			</div>	\n" +
    "			\n" +
    "			<div class=\"alert alert-warning\">\n" +
    "				<h4 class=\"alert-heading\">Response</h4>\n" +
    "				<p class=\"long-wrap\">\n" +
    "				<code>\n" +
    "\n" +
    "				{\"x\":-0.35434186,\"y\":1.2737153,\"z\":9.615114,\n" +
    "				</code>\n" +
    "				<br/>\n" +
    "				<code>\n" +
    "				\"timestamp\":\"2016-09-24T22:41:01GMT+08:00\",\n" +
    "				</code>\n" +
    "				<br/>\n" +
    "				<code>\n" +
    "				\"description\":\"\",\"requestMethod\":\"GET\",\"isSuccessful\":true\n" +
    "				</code>\n" +
    "				<br/>\n" +
    "				<code>\n" +
    "				}\n" +
    "				</code>\n" +
    "				</p>\n" +
    "			</div>\n" +
    "\n" +
    "			<table id=\"sms_api_doc\" class=\"table table-striped\">\n" +
    "			<thead>\n" +
    "				<tr>\n" +
    "					<th>Field</th>\n" +
    "					<th>Description</th>\n" +
    "				</tr>\n" +
    "			</thead>\n" +
    "			\n" +
    "			<tbody>   \n" +
    "				<tr>\n" +
    "					<td>\n" +
    "						x\n" +
    "					</td>\n" +
    "				<td>\n" +
    "					X axis.\n" +
    "				</td>\n" +
    "				</tr>\n" +
    "				<tr>\n" +
    "					<td>\n" +
    "						y\n" +
    "					</td>\n" +
    "				<td>\n" +
    "					Y axis.\n" +
    "				</td>\n" +
    "				</tr>\n" +
    "				<tr>\n" +
    "					<td>\n" +
    "						z\n" +
    "					</td>\n" +
    "				<td>\n" +
    "					Z axis.\n" +
    "				</td>\n" +
    "				</tr>\n" +
    "				<tr>\n" +
    "					<td>\n" +
    "						timestamp\n" +
    "					</td>\n" +
    "				<td>\n" +
    "					Timestamp the sensor data is captured.\n" +
    "				</td>\n" +
    "				</tr> 	      \n" +
    "				<tr id=\"description\">\n" +
    "					<td>\n" +
    "						description\n" +
    "					</td>\n" +
    "				<td>\n" +
    "					Contains the error message if isSuccessful is \"false\".\n" +
    "				</td>\n" +
    "				</tr>\n" +
    "				\n" +
    "				<tr id=\"requestMethod\">\n" +
    "					<td>\n" +
    "						requestMethod\n" +
    "					</td>\n" +
    "				<td>\n" +
    "					Request method. Default to \"GET\", \"POST\" or \"DELETE\" depending on the HTTP method.\n" +
    "				</td>\n" +
    "				</tr>\n" +
    "				\n" +
    "				<tr id=\"isSuccessful\">\n" +
    "					<td>\n" +
    "						isSuccessful\n" +
    "					</td>\n" +
    "				<td>\n" +
    "					true if operation is successful. Otherwise returns false.\n" +
    "				</td>\n" +
    "				</tr>\n" +
    "				\n" +
    "				\n" +
    "			</tbody>\n" +
    "			</table>\n" +
    "\n" +
    "\n" +
    "			<!--\n" +
    "			<nvd3 options=\"options\"\n" +
    "		      data=\"data\"\n" +
    "		      config=\"config\"\n" +
    "		      events=\"events\"\n" +
    "		      api=\"api\"\n" +
    "		      on-ready=\"callback\"></nvd3>\n" +
    "			-->\n" +
    "			\n" +
    "		</div>\n" +
    "	</div>\n" +
    "</div>");
}]);

angular.module("services/services.tpl.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("services/services.tpl.html",
    "<div class=\"container-fluid\">\n" +
    "	<div class=\"row\">\n" +
    "		<div class=\"col-md-3\" id=\"sidebar\" ng-include=\"'assets/view/services_sidebar.html'\"></div>\n" +
    "		\n" +
    "		\n" +
    "		<div class=\"col-md-9\" id=\"content\">\n" +
    "		\n" +
    "			<div class=\"alert alert-info\">\n" +
    "				<h3 class=\"alert-heading\">Status API</h3>\n" +
    "			</div>\n" +
    "			\n" +
    "			<div class=\"alert alert-success\">\n" +
    "				<h4 class=\"alert-heading\"> <button class=\"btn btn-default btn-xs\">GET</button> Check Service Status </h4> \n" +
    "				<p></p> <br>\n" +
    "				<p class=\"long-wrap\">\n" +
    "				<a ng-href=\"{{statusLink}}\">{{statusLink}}</a><b>{optional parameter}</b>\n" +
    "				</p>\n" +
    "			</div>\n" +
    "			\n" +
    "			<table id=\"sms_api_doc\" class=\"table table-striped\">\n" +
    "				<thead>\n" +
    "					<tr>\n" +
    "						<th>Parameter</th>\n" +
    "						<th>Description</th>\n" +
    "					</tr>\n" +
    "				</thead>\n" +
    "				\n" +
    "				<tbody>    	      \n" +
    "					<tr id=\"To\">\n" +
    "						<td nowrap>\n" +
    "							optional parameter\n" +
    "						</td>\n" +
    "					<td>\n" +
    "					Specify the optional parameter to retrieve specific information.\n" +
    "					<br/><br/>\n" +
    "					\n" +
    "					<dl class=\"dl-horizontal\">\n" +
    "					  <dt>network</dt>\n" +
    "					  <dd>Network information only. <br/>For dual SIM phone, to retrieve additional information for a particular SIM, pass in the slot parameter, e.g. /network?slot=1. Accepted slot values are \"1\" or \"2\".</dd>\n" +
    "					  <dt>battery</dt>\n" +
    "					  <dd>Battery information only.</dd>\n" +
    "					</dl>\n" +
    "					</td>\n" +
    "					</tr>\n" +
    "				</tbody>\n" +
    "			</table>\n" +
    "	\n" +
    "			\n" +
    "			<div class=\"alert alert-warning\">\n" +
    "				<h4 class=\"alert-heading\">Response</h4>\n" +
    "			\n" +
    "				<p class=\"long-wrap\">\n" +
    "				<code>\n" +
    "				\"deviceName\":\"Samsung GT-I8190N\",\n" +
    "				</code>\n" +
    "				<br/>\n" +
    "				<code>\n" +
    "				{\"batteryInfo\":{\"health\":\"GOOD\",\"technology\":\"Li-ion\",\"status\":\"CHARGING\",\"plugType\":\"USB\", \n" +
    "				</code>\n" +
    "				<br/>\n" +
    "				<code>\n" +
    "				\"level\":93,\"isPresent\":true,\"temperature\":306,\"voltage\":4337},\n" +
    "				</code><br/><code>\n" +
    "				\"networkInfo\":{\"cellLocationType\":\"GSM\",\"connectionType\":\"WIFI\",\"deviceId\":\"355074050397674\",\n" +
    "				</code><br/><code>\n" +
    "				\"deviceSoftwareVersion\":\"01\",\"gsmCellLocation\":{\"cid\":1411909,\"lac\":33212,\"psc\":-1},\n" +
    "				</code><br/><code>\n" +
    "				\"ipAddress\":\"192.168.0.101\",\"voiceMailNumber\":\"121\",\"subscriberId\":\"502166904880510\",\n" +
    "				</code><br/><code>\n" +
    "				\"networkCountryIso\":\"my\",\"networkOperator\":\"50216\",\"networkOperatorName\":\"\",\n" +
    "				</code><br/><code>\n" +
    "				\"networkType\":\"HSDPA\",\"phoneNumber\":\"\",\"phoneType\":\"GSM\",\n" +
    "				</code><br/><code>\n" +
    "				\"signalStrength\":{\"cdmaDbm\":-1,\"cdmaEcio\":-1,\"evdoDbm\":-1,\"evdoEcio\":-1,\"evdoSnr\":-1,\n" +
    "				</code><br/><code>\n" +
    "				\"gsmBitErrorRate\":-1,\"gsmSignalStrength\":99,\"isGsm\":true},\"simCountryIso\":\"my\",\n" +
    "				</code><br/><code>\n" +
    "				\"simOperator\":\"50216\",\"simOperatorName\":\"DiGi\",\"simSerialNumber\":\"89601611012681166075\",\n" +
    "				</code><br/><code>\n" +
    "				\"simState\":\"READY\",\"isNetworkRoaming\":false,\"isConnected\":true},\"description\":\"\",\n" +
    "				</code><br/><code>\n" +
    "				\"requestMethod\":\"GET\",\"isSuccessful\":true}\n" +
    "				</code>\n" +
    "				\n" +
    "				</p>\n" +
    "				\n" +
    "			</div>\n" +
    "			\n" +
    "			\n" +
    "			<table id=\"sms_api_doc\" class=\"table table-striped\">\n" +
    "			<thead>\n" +
    "				<tr>\n" +
    "					<th>Field</th>\n" +
    "					<th>Description</th>\n" +
    "				</tr>\n" +
    "			</thead>\n" +
    "			\n" +
    "			<tbody>    	    \n" +
    "			\n" +
    "				<tr>\n" +
    "					<td>\n" +
    "						deviceName\n" +
    "					</td>\n" +
    "					\n" +
    "				<td>\n" +
    "					Device name.\n" +
    "				</td>\n" +
    "				</tr>\n" +
    "\n" +
    "				<tr id=\"battery\">\n" +
    "					<td>\n" +
    "						batteryInfo\n" +
    "					</td>\n" +
    "					\n" +
    "				<td>\n" +
    "					Battery information.\n" +
    "					\n" +
    "					<br/><br/>\n" +
    "					\n" +
    "					<dl class=\"dl-horizontal\">\n" +
    "					  <dt>health</dt>\n" +
    "					  <dd>Possible values are COLD, DEAD, GOOD, OVERHEAT, OVER_VOLTAGE, UNKNOWN, and UNSPECIFIED_FAILURE.</dd>\n" +
    "					  <dt>technology</dt>\n" +
    "					  <dd>Battery technology.</dd>\n" +
    "					  <dt>status</dt>\n" +
    "					  <dd>Possible values are CHARGING, DISCHARGING, FULL, NOT_CHARGING, and UNKNOWN.</dd>\n" +
    "					  <dt>plugType</dt>\n" +
    "					  <dd>Possible values are AC, USB, and WIRELESS.</dd>\n" +
    "					  <dt>level</dt>\n" +
    "					  <dd>Battery charge level.</dd>\n" +
    "					  <dt>isPresent</dt>\n" +
    "					  <dd>Indicates if a battery is available.</dd>\n" +
    "					  <dt>temperature</dt>\n" +
    "					  <dd>Battery temperature in tenths of a degree centigrade.</dd>\n" +
    "					  <dt>voltage</dt>\n" +
    "					  <dd>Battery voltage in millivolts.</dd>\n" +
    "					</dl>\n" +
    "					\n" +
    "					\n" +
    "				</td>\n" +
    "				</tr>\n" +
    "				\n" +
    "				<tr id=\"network\">\n" +
    "					<td>\n" +
    "						networkInfo\n" +
    "					</td>\n" +
    "					\n" +
    "				<td>\n" +
    "					Network information.\n" +
    "					<br/><br/>\n" +
    "					\n" +
    "					<dl class=\"dl-horizontal\">\n" +
    "					  <dt>cellLocationType</dt>\n" +
    "					  <dd>Current location type of the device. Can be either gsmCellLocation or cdmaCellLocation.</dd>\n" +
    "					 \n" +
    "					  <dt>gsmCellLocation</dt>\n" +
    "					  <dd>\n" +
    "						 GSM cell location information if cellLocationType is GSM.<br/>\n" +
    "						 <br/>\n" +
    "						  <ul style=\"margin-left:40px\">\n" +
    "							<li style=\"list-style:circle\">cid - GSM cell id, -1 if unknown, 0xffff max legal value.</li>\n" +
    "							<li style=\"list-style:circle\">lac - GSM location area code, -1 if unknown, 0xffff max legal value.</li>\n" +
    "							<li style=\"list-style:circle\">psc - Primary scrambling code for UMTS, -1 if unknown or GSM.</li>\n" +
    "						  </ul>\n" +
    "						  \n" +
    "						  <br/>\n" +
    "					  </dd>\n" +
    "					  \n" +
    "					  <dt>cdmaCellLocation</dt>\n" +
    "					  <dd>\n" +
    "						CDMA cell location information if cellLocationType is CDMA.<br/>\n" +
    "						 <br/>\n" +
    "						  <ul style=\"margin-left:40px\">\n" +
    "							<li style=\"list-style:circle\">baseStationId - CDMA base station identification number, -1 if unknown.</li>\n" +
    "							<li style=\"list-style:circle\">baseStationLatitude - CDMA base station latitude in units of 0.25 seconds, Integer.MAX_VALUE if unknown.</li>\n" +
    "							<li style=\"list-style:circle\">baseStationLongtitude - CDMA base station longitude in units of 0.25 seconds, Integer.MAX_VALUE if unknown.</li>\n" +
    "							<li style=\"list-style:circle\">networkId - CDMA network identification number, -1 if unknown.</li>\n" +
    "							<li style=\"list-style:circle\">systemId - CDMA system identification number, -1 if unknown.</li>\n" +
    "						  </ul>\n" +
    "						  \n" +
    "						  <br/>\n" +
    "					  </dd>\n" +
    "					  \n" +
    "					  \n" +
    "					  <dt>connectionType</dt>\n" +
    "					  <dd>Possible values are WIFI and MOBILE.</dd>\n" +
    "					  \n" +
    "					  <dt>deviceId</dt>\n" +
    "					  <dd>Returns the unique device ID, for example, the IMEI for GSM and the MEID or ESN for CDMA phones. </dd>\n" +
    "					  \n" +
    "					  <dt>deviceSoftwareVersion</dt>\n" +
    "					  <dd>Returns the software version number for the device, for example, the IMEI/SV for GSM phones.</dd>\n" +
    "					  \n" +
    "					  <dt>ipAddress</dt>\n" +
    "					  <dd>IP address.</dd>\n" +
    "					  \n" +
    "					  <dt>voiceMailNumber</dt>\n" +
    "					  <dd>Returns the voice mail number.</dd>\n" +
    "					  \n" +
    "					   <dt>subscriberId</dt>\n" +
    "					  <dd>Returns the unique subscriber ID, for example, the IMSI for a GSM phone.</dd>\n" +
    "					  \n" +
    "					   <dt>networkCountryIso</dt>\n" +
    "					  <dd>Returns the ISO country code equivalent of the current registered operator's MCC (Mobile Country Code).</dd>\n" +
    "					  \n" +
    "					   <dt>networkOperator</dt>\n" +
    "					  <dd>Returns the numeric name (MCC+MNC) of current registered operator.</dd>\n" +
    "					  \n" +
    "					   <dt>networkOperatorName</dt>\n" +
    "					  <dd>Returns the alphabetic name of current registered operator.</dd>\n" +
    "					  \n" +
    "					  <dt>networkType</dt>\n" +
    "					  <dd>The network type for current data connection. \n" +
    "					  Possible values are 1xRTT, CDMA, EDGE, EHRPD, EVDO_0, EVDO_A, EVDO_B,\n" +
    "					  GPRS, HSDPA, HPSA, HSPAP, HSUPA, IDEN, LTE, UMTS, and UNKNOWN.\n" +
    "					  </dd>\n" +
    "					  \n" +
    "					  <dt>phoneNumber</dt>\n" +
    "					  <dd>Phone number.</dd>\n" +
    "					  \n" +
    "					   <dt>phoneType</dt>\n" +
    "					  <dd>Phone type. Possible valus are GSM, CDMA, SIP or NONE.</dd>\n" +
    "					  \n" +
    "					  <dt>signalStrength</dt>\n" +
    "					  <dd>Signal strength.\n" +
    "						<br/>\n" +
    "						  <ul style=\"margin-left:40px\">\n" +
    "							<li style=\"list-style:circle\">cdmaDbm - Get the CDMA RSSI value in dBm. </li>\n" +
    "							<li style=\"list-style:circle\">cdmaEcio - Get the CDMA Ec/Io value in dB*10.</li>\n" +
    "							<li style=\"list-style:circle\">evdoDbm - Get the EVDO RSSI value in dBm.</li>\n" +
    "							<li style=\"list-style:circle\">evdoEcio - Get the EVDO Ec/Io value in dB*10.</li>\n" +
    "							<li style=\"list-style:circle\">evdoSnr - Get the signal to noise ratio. Valid values are 0-8. 8 is the highest.</li>\n" +
    "							<li style=\"list-style:circle\">gsmBitErrorRate - Get the GSM bit error rate (0-7, 99) as defined in TS 27.007 8.5.</li>\n" +
    "							<li style=\"list-style:circle\">gsmSignalStrength - Get the GSM Signal Strength, valid values are (0-31, 99) as defined in TS 27.007 8.5.</li>\n" +
    "							<li style=\"list-style:circle\">isGsm - true if this is for GSM.</li>\n" +
    "						  </ul>					  \n" +
    "					  </dd>\n" +
    "\n" +
    "					  <dt>simCountryIso</dt>\n" +
    "					  <dd>Returns the ISO country code equivalent for the SIM provider's country code.</dd> \n" +
    "\n" +
    "					  <dt>simOperator</dt>\n" +
    "					  <dd>Returns the MCC+MNC (mobile country code + mobile network code) of the provider of the SIM.</dd>\n" +
    "					  \n" +
    "					    <dt>simOperatorName</dt>\n" +
    "					  <dd>Returns the Service Provider Name (SPN).</dd>\n" +
    "					  \n" +
    "					  <dt>simSerialNumber</dt>\n" +
    "					  <dd>Returns the serial number of the SIM, if applicable.</dd>\n" +
    "					  \n" +
    "					  <dt>simState</dt>\n" +
    "					  <dd>Returns the state of the device SIM card. Possible values are ABSENT, NETWORK_LOCKED, PIN_REQUIRED, PUK_REQUIRED, READY, UNKNOWN.					  \n" +
    "					  </dd>\n" +
    "					  \n" +
    "					  <dt>isNetworkRoaming</dt>\n" +
    "					  <dd>Returns true if the device is considered roaming on the current network, for GSM purposes.</dd>\n" +
    "					  \n" +
    "					  <dt>isConnected</dt>\n" +
    "					  <dd>Indicates if the phone is connected to a network (WIFI or mobile).</dd>\n" +
    "					  \n" +
    "					  \n" +
    "					</dl>\n" +
    "				</td>\n" +
    "				</tr>\n" +
    "				\n" +
    "				<tr id=\"description\">\n" +
    "					<td>\n" +
    "						description\n" +
    "					</td>\n" +
    "				<td>\n" +
    "					Contains the error message if isSuccessful is \"false\".\n" +
    "				</td>\n" +
    "				</tr>\n" +
    "				\n" +
    "				<tr id=\"requestMethod\">\n" +
    "					<td>\n" +
    "						requestMethod\n" +
    "					</td>\n" +
    "				<td>\n" +
    "					Request method. Default to \"GET\", \"POST\" or \"DELETE\" depending on the HTTP method.\n" +
    "				</td>\n" +
    "				</tr>\n" +
    "				\n" +
    "				<tr id=\"requestMethod\">\n" +
    "					<td>\n" +
    "						isSuccessful\n" +
    "					</td>\n" +
    "				<td>\n" +
    "					true if operation is successful. Otherwise returns false.\n" +
    "				</td>\n" +
    "				</tr>\n" +
    "				\n" +
    "			</tbody>\n" +
    "			</table>			\n" +
    "		\n" +
    "		\n" +
    "		</div>\n" +
    "	</div>\n" +
    "	\n" +
    "	\n" +
    "</div>\n" +
    "					\n" +
    "	\n" +
    "		\n" +
    "		\n" +
    "	\n" +
    "");
}]);

angular.module("surveillance/surveillance.tpl.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("surveillance/surveillance.tpl.html",
    "<form name=\"surveillance_form\">\n" +
    "	<div class=\"row\">\n" +
    "\n" +
    "		<div ng-style=\"{height: videoPanelFiller + 'px', width: '0px'}\"></div>\n" +
    "		<div class=\"video_view\" id=\"video_plane\" video-src=\"videoSrc\"\n" +
    "			videoplayer rotate degrees=\"rotationDegree\"\n" +
    "			ng-style=\"{height: videoSrc.height + 'px', width: videoSrc.width + 'px'}\" uib-tooltip-placement=\"top\" uib-tooltip=\"Camera display screen\"></div>\n" +
    "		<div ng-style=\"{height: videoPanelFiller + 'px', width: '0px'}\"></div>\n" +
    "\n" +
    "\n" +
    "		<br/>\n" +
    "\n" +
    "		<div class=\"container\">\n" +
    "			<div class=\"col-md-6 col-md-offset-3\">\n" +
    "				<input name=\"btn-play-control\" type=\"hidden\"\n" +
    "					ng-model=\"playControlFlag\" required> <input id=\"btn-camera\"\n" +
    "					type=\"button\"\n" +
    "					class=\"btn btn-block form-control btn-default btn-lg\"\n" +
    "					value=\"{{ !inStreaming ? 'Turn on' : 'Turn off'}}\"\n" +
    "					ng-click=\"playClick($event)\"\n" +
    "					ng-disabled=\"surveillance_form.$invalid\" uib-tooltip-placement=\"top\" uib-tooltip=\"On/off the camera\">\n" +
    "			</div>\n" +
    "		</div>	\n" +
    "		\n" +
    "		<div ng-show=\"debugMsg != ''\">\n" +
    "			<br/>\n" +
    "		</div>\n" +
    "		<div class=\"row alert alert-warning\"  ng-show=\"debugMsg != ''\">			\n" +
    "			<p class=\"text-center\">{{debugMsg}}</p>\n" +
    "		</div>\n" +
    "		\n" +
    "		<toaster-container toaster-options=\"{'position-class': 'toast-center'}\"></toaster-container>\n" +
    "            \n" +
    "		<hr />\n" +
    "		<div class=\"audio_view\" ng-show=\"enableAudio\">\n" +
    "\n" +
    "			<div id=\"jquery_jplayer_1\" class=\"jp-jplayer\" data-audio=\"audioSrc\"\n" +
    "				data-autoplay=\"autoPlay\" data-pauseothers=\"true\" jplayer></div>\n" +
    "\n" +
    "			<div id=\"jp_container_1\" class=\"jp-audio\" role=\"application\"\n" +
    "				aria-label=\"media player\">\n" +
    "				<div class=\"jp-type-single\">\n" +
    "					<div class=\"jp-gui jp-interface\">\n" +
    "						<div class=\"jp-controls\">\n" +
    "							<button class=\"jp-play\" role=\"button\" tabindex=\"0\">play</button>\n" +
    "							<button class=\"jp-stop\" role=\"button\" tabindex=\"0\">stop</button>\n" +
    "						</div>\n" +
    "						<div class=\"jp-progress\">\n" +
    "							<div class=\"jp-seek-bar\">\n" +
    "								<div class=\"jp-play-bar\"></div>\n" +
    "							</div>\n" +
    "						</div>\n" +
    "						<div class=\"jp-volume-controls\">\n" +
    "							<button class=\"jp-mute\" role=\"button\" tabindex=\"0\">mute</button>\n" +
    "							<button class=\"jp-volume-max\" role=\"button\" tabindex=\"0\">max\n" +
    "								volume</button>\n" +
    "							<div class=\"jp-volume-bar\">\n" +
    "								<div class=\"jp-volume-bar-value\"></div>\n" +
    "							</div>\n" +
    "						</div>\n" +
    "						<div class=\"jp-time-holder\">\n" +
    "							<div class=\"jp-current-time\" role=\"timer\" aria-label=\"time\">&nbsp;</div>\n" +
    "							<div class=\"jp-duration\" role=\"timer\" aria-label=\"duration\">&nbsp;</div>\n" +
    "							<div class=\"jp-toggles\">\n" +
    "								<button class=\"jp-repeat\" role=\"button\" tabindex=\"0\">repeat</button>\n" +
    "							</div>\n" +
    "						</div>\n" +
    "					</div>\n" +
    "					<div class=\"jp-details\">\n" +
    "						<div class=\"jp-title\" aria-label=\"title\">&nbsp;</div>\n" +
    "					</div>\n" +
    "					<div class=\"jp-no-solution\">\n" +
    "						<span>Update Required</span> To play the media you will need to\n" +
    "						either update your browser to a recent version or update your \n" +
    "						<a href=\"http://get.adobe.com/flashplayer/\" target=\"_blank\">Flash plugin</a>.\n" +
    "					</div>\n" +
    "				</div>\n" +
    "			</div>\n" +
    "			<br/>\n" +
    "		</div>		\n" +
    "	</div>\n" +
    "\n" +
    "	<div class=\"container\">\n" +
    "\n" +
    "		 <div class=\"row-fluid\">		 	\n" +
    "		 	<div class=\"btn-group btn-group-justified\">\n" +
    "	 				<a href class=\"btn btn-default btn-sm col-sm-3 col-xs-6\" ng-click=\"toggleMotion()\">\n" +
    "						<i class='fa fa-spinner fa-spin' uib-tooltip-placement=\"top\" uib-tooltip=\"Show detected motion if motion detection is enabled\"></i><br/><span class=\"hidden-xs\" >Motion View</span>\n" +
    "					</a>\n" +
    "					<a href class=\"btn btn-default  btn-sm col-sm-3 col-xs-6\" ng-click=\"toggleCamera()\">\n" +
    "						<i class='fa fa-camera' uib-tooltip-placement=\"top\" uib-tooltip=\"Switch between front and back camera\"></i><br/><span class=\"hidden-xs\">Switch Camera</span>\n" +
    "					</a>            \n" +
    "					<a  href class=\"btn btn-default  btn-sm col-sm-3 col-xs-6\" ng-click=\"rotateImage()\">\n" +
    "						<i class='fa fa-repeat' uib-tooltip-placement=\"top\" uib-tooltip=\"Rotate the display screen\"></i><br/>\n" +
    "						<span class=\"hidden-xs\">Rotate</span>\n" +
    "					</a>\n" +
    "\n" +
    "					<!-- <a href class=\"btn btn-default btn-sm col-sm-3 col-xs-6\" ng-click=\"showSnapshot()\">\n" +
    "						<i class='glyphicon glyphicon-screenshot'></i><br/><span class=\"hidden-xs\">Snapshot</span>\n" +
    "					</a> -->\n" +
    "					\n" +
    "					<div class=\"btn-group\">\n" +
    "					  <button type=\"button\" class=\"btn btn-default btn-sm col-sm-3 col-xs-6 dropdown-toggle\" data-toggle=\"dropdown\" aria-haspopup=\"true\" aria-expanded=\"false\">\n" +
    "					    <i class='glyphicon glyphicon-screenshot' uib-tooltip-placement=\"top\" uib-tooltip=\"Take a camera photo\"></i><br/><span class=\"hidden-xs\">Snapshot</span> <span class=\"caret\"></span>\n" +
    "					  </button>\n" +
    "					  <ul class=\"dropdown-menu\">\n" +
    "					    <li><a href ng-click=\"showSnapshot()\" uib-tooltip-placement=\"top\" uib-tooltip=\"Capture and display a snaphot in a new browser\">Show photo in browser</a></li>\n" +
    "					    <li><a href ng-click=\"takePhoto()\" uib-tooltip-placement=\"top\" uib-tooltip=\"Take and save a photo\">Take photo</a></li>\n" +
    "					    <!--\n" +
    "					    <li><a href uib-tooltip-placement=\"top\" uib-tooltip=\"Take and save a focused photo\">Take focused photo</a></li>-->\n" +
    "\n" +
    "					  </ul>\n" +
    "					</div>\n" +
    "\n" +
    "					<a href ng-click=\"showFullScreen()\" class=\"btn btn-default btn-sm col-sm-3 col-xs-6\">\n" +
    "						<i class='fa fa-arrows' uib-tooltip-placement=\"top\" uib-tooltip=\"Full screen mode\"></i><br/><span class=\"hidden-xs\">Full screen</span>\n" +
    "					</a>\n" +
    "					<a href=\"#image\" class=\"btn btn-default btn-sm col-sm-3 col-xs-6\">\n" +
    "						<i class='fa fa-image' uib-tooltip-placement=\"top\" uib-tooltip=\"Images in the device\"></i><br/><span class=\"hidden-xs\">Image Gallery</span>\n" +
    "					</a>\n" +
    "					<a href=\"#video\" class=\"btn btn-default btn-sm col-sm-3 col-xs-6\">\n" +
    "						<i class='fa fa-video-camera' uib-tooltip-placement=\"top\" uib-tooltip=\"Videos in the device\"></i><br/><span class=\"hidden-xs\">Video Gallery</span>\n" +
    "					</a>\n" +
    "					<a href class=\"btn btn-default btn-sm col-sm-3 col-xs-6\" ng-click=\"showMediaInfo()\">\n" +
    "						<i class='glyphicon glyphicon-info-sign' uib-tooltip-placement=\"top\" uib-tooltip=\"Video and audio URLs\"></i><br/><span class=\"hidden-xs\">Info</span>\n" +
    "					</a>\n" +
    "             </div>\n" +
    "        </div>\n" +
    "\n" +
    "        <br/>\n" +
    "		<div class=\"row-fluid\">			\n" +
    "				<label class=\"checkbox-inline\">\n" +
    "					<input type=\"checkbox\" id=\"checkbox_led\" ng-model=\"enableLED\"\n" +
    "						ng-click=\"selectLED($event)\">Flash\n" +
    "				</label>\n" +
    "				<label class=\"checkbox-inline\">\n" +
    "					<input type=\"checkbox\" id=\"checkbox_audio\" ng-model=\"enableAudio\"\n" +
    "						ng-click=\"selectAudio($event)\">Audio\n" +
    "				</label>\n" +
    "				<label class=\"checkbox-inline\">\n" +
    "					<input type=\"checkbox\" id=\"checkbox_disguise\" ng-model=\"enableCameraDisguise\" ng-click=\"disguiseCamera($event)\">Disguise\n" +
    "				</label>\n" +
    "				<label class=\"checkbox-inline\">\n" +
    "					<input type=\"checkbox\" id=\"checkbox_autoexposurelock\" ng-model=\"autoExposureLock\"\n" +
    "						ng-click=\"selectAutoExposureLock($event)\">Auto exposure lock\n" +
    "				</label>\n" +
    "				<label class=\"checkbox-inline\">\n" +
    "					<input type=\"checkbox\" id=\"checkbox_autoresizescreen\" ng-model=\"autoResizeScreen\"\n" +
    "						ng-click=\"selectAutoResizeScreen($event)\">Auto resize screen\n" +
    "				</label>\n" +
    "				<label class=\"checkbox-inline\">\n" +
    "					<input type=\"checkbox\" id=\"checkbox_motionDetection\" ng-model=\"motionDetection\"\n" +
    "						ng-click=\"selectMotionDetection($event)\">Motion detection\n" +
    "				</label>				\n" +
    "		</div>\n" +
    "\n" +
    "		<br/>\n" +
    "		\n" +
    "		<form class=\"form-horizontal\">\n" +
    "		\n" +
    "			<div class=\"row\">\n" +
    "				<label for=\"range_screen_size\" class=\"col-xs-3\">Screen size</label>\n" +
    "				<div class=\"col-md-5 col-xs-7\">\n" +
    "					<input id=\"range_screen_size\" class=\"col-xs-6 col-md-4\" type=\"range\"\n" +
    "						min=\"10\" max=\"300\" step=\"1\" ng-model=\"data.screenSize\"\n" +
    "						value=\"{{data.screenSize}}\" ng-change=\"selectScreenSize()\"  ng-model-options=\"{ debounce: 500 }\" uib-tooltip-placement=\"top\" uib-tooltip=\"Uncheck Auto resize screen to manually adjust the screen size\">\n" +
    "				</div>\n" +
    "				<label for=\"range_screen_size\" id=\"range_screen_size_label\" class=\"control-label col-xs-2\">{{data.screenSize}}%</label>\n" +
    "			</div>\n" +
    "			\n" +
    "			<br/>\n" +
    "				\n" +
    "			<div class=\"row\">\n" +
    "				<label for=\"range_zoom\" class=\"col-xs-3\">Zoom</label>\n" +
    "				<div class=\"col-md-5 col-xs-7\">\n" +
    "					<input id=\"range_zoom\" class=\"col-xs-6 col-md-4\" type=\"range\"\n" +
    "						min=\"0\" max=\"100\" step=\"1\" ng-model=\"data.zoomLevel\"\n" +
    "						value=\"{{data.zoomLevel}}\" ng-change=\"selectZoom()\" ng-model-options=\"{ debounce: 500 }\">\n" +
    "				</div>\n" +
    "				<label for=\"range_zoom\" id=\"range_zoom_label\" class=\"control-label col-xs-2\">{{data.zoomLevel}}%</label>\n" +
    "			</div>\n" +
    "	\n" +
    "			<br/>\n" +
    "	\n" +
    "			<div class=\"row\">\n" +
    "				<label for=\"range_quality\" class=\"col-xs-3\">Image\n" +
    "					quality</label>\n" +
    "				<div class=\"col-md-5 col-xs-7\">\n" +
    "					<input id=\"range_quality\" class=\"col-xs-6 col-md-4\" type=\"range\"\n" +
    "						min=\"1\" max=\"100\" ng-model=\"data.imageQuality\"\n" +
    "						value=\"{{data.imageQuality}}\" ng-change=\"selectImageQuality()\" ng-model-options=\"{ debounce: 500 }\">\n" +
    "				</div>\n" +
    "				<label for=\"range_quality\" id=\"range_quality_label\" class=\"control-label col-xs-2\">{{data.imageQuality}}</label>\n" +
    "			</div>\n" +
    "	\n" +
    "			<br/>\n" +
    "			<div class=\"row\">\n" +
    "				<label for=\"range_exposure\" class=\"col-xs-3\">Exposure compensation</label>\n" +
    "				<div class=\"col-md-5 col-xs-7\">\n" +
    "					<input id=\"range_exposure\" class=\"col-xs-6 col-md-4\" type=\"range\"\n" +
    "						min=\"{{exposureCompensation.min}}\" max=\"{{exposureCompensation.max}}\" step=\"1\" ng-model=\"exposureCompensation.current\"\n" +
    "						value=\"{{exposureCompensation.current}}\" ng-change=\"selectExposureCompensation()\" ng-model-options=\"{ debounce: 500 }\">\n" +
    "				</div>\n" +
    "				<label for=\"range_exposure\" id=\"range_exposure_label\" class=\"control-label col-xs-2\">{{exposureCompensation.current}}</label>\n" +
    "			</div>\n" +
    "\n" +
    "			<br/>\n" +
    "			<div class=\"row\">\n" +
    "				<label for=\"range_motion_detection_threshold\" class=\"col-xs-3\">Motion detection threshold</label>\n" +
    "				<div class=\"col-md-5 col-xs-7\">\n" +
    "					<input id=\"range_motion_detection_threshold\" class=\"col-xs-6 col-md-4\" type=\"range\"\n" +
    "						min=\"1\" max=\"255\" step=\"1\" ng-model=\"motionDetectionThreshold\"\n" +
    "						value=\"{{motionDetectionThreshold}}\" ng-change=\"selectMotionDetectionThreshold()\" ng-model-options=\"{ debounce: 500 }\">\n" +
    "				</div>\n" +
    "				<label for=\"range_motion_detection_threshold\" id=\"range_motion_detection_threshold_label\" class=\"control-label col-xs-2\">{{motionDetectionThreshold}}</label>\n" +
    "			</div>\n" +
    "		\n" +
    "		</form>	\n" +
    "		\n" +
    "		\n" +
    "		<br/>\n" +
    "			\n" +
    "		<h4 class=\"panel-title\">\n" +
    "            <a data-toggle=\"collapse\" id=\"camera_settings\" href ng-click=\"selectCameraSettings()\">\n" +
    "              Camera settings <b class=\"caret\"></b>\n" +
    "            </a>\n" +
    "         </h4>\n" +
    "          \n" +
    "        \n" +
    "        <form class=\"form-horizontal\">\n" +
    "			<div class=\"row checkbox_view\" ng-show=\"cameraSettings\">\n" +
    "					<div class=\"row\">\n" +
    "						<label for=\"resolution_select\" class=\"col-xs-3\">Resolution</label>\n" +
    "						<div class=\"col-md-5 col-xs-7\">\n" +
    "			              <div class=\"input-group\">\n" +
    "			                <select class=\"form-control\" ng-model=\"resolutionValue\"\n" +
    "								ng-options=\"res | resFilter:this for res in resolutions\"\n" +
    "								ng-change=\"selectRes()\"></select>\n" +
    "			              </div>\n" +
    "			            </div>\n" +
    "					</div>\n" +
    "				\n" +
    "					<div class=\"row\">\n" +
    "						<label for=\"scene_mode_select\" class=\"col-xs-3\">Scene mode</label>\n" +
    "						<div class=\"col-md-5 col-xs-7\">\n" +
    "			              <div class=\"input-group\">\n" +
    "				              <select class=\"form-control\" ng-model=\"sceneMode\"\n" +
    "									ng-options=\"s as s for s in sceneModes\"\n" +
    "									ng-change=\"selectSceneMode()\"></select>\n" +
    "			              </div>\n" +
    "			            </div>\n" +
    "					</div>\n" +
    "					\n" +
    "					<div class=\"row\">\n" +
    "						<label for=\"color_effect_select\" class=\"col-xs-3\">Color effect</label>\n" +
    "						<div class=\"col-md-5 col-xs-7\">\n" +
    "			              <div class=\"input-group\">\n" +
    "				            <select class=\"form-control\" ng-model=\"colorEffect\"\n" +
    "								ng-options=\"c as c for c in colorEffects\"\n" +
    "								ng-change=\"selectColorEffect()\"></select>\n" +
    "			              </div>\n" +
    "			            </div>\n" +
    "					</div>\n" +
    "					\n" +
    "					\n" +
    "					<div class=\"row\">\n" +
    "						<label for=\"flash_mode_select\" class=\"col-xs-3\">Flash mode</label>\n" +
    "						<div class=\"col-md-5 col-xs-7\">\n" +
    "			              <div class=\"input-group\">\n" +
    "				           <select class=\"form-control\" ng-model=\"flashMode\"\n" +
    "								ng-options=\"f as f for f in flashModes\"\n" +
    "								ng-change=\"selectFlashMode()\"></select>\n" +
    "			              </div>\n" +
    "			            </div>\n" +
    "					</div>\n" +
    "					\n" +
    "					<div class=\"row\">\n" +
    "						<label for=\"focus_mode_select\" class=\"col-xs-3\">Focus mode</label>\n" +
    "						<div class=\"col-md-5 col-xs-7\">\n" +
    "			              <div class=\"input-group\">\n" +
    "				           <select class=\"form-control\" ng-model=\"focusMode\"\n" +
    "								ng-options=\"f as f for f in focusModes\"\n" +
    "								ng-change=\"selectFocusMode()\"></select>\n" +
    "			              </div>\n" +
    "			            </div>\n" +
    "					</div>\n" +
    "					\n" +
    "					<div class=\"row\">\n" +
    "						<label for=\"white_balance_select\" class=\"col-xs-3\">White balance</label>\n" +
    "						<div class=\"col-md-5 col-xs-7\">\n" +
    "			              <div class=\"input-group\">\n" +
    "				       	 	<select class=\"form-control\" ng-model=\"whiteBalance\"\n" +
    "								ng-options=\"f as f for f in whiteBalances\"\n" +
    "								ng-change=\"selectWhiteBalance()\"></select>\n" +
    "			              </div>\n" +
    "			            </div>\n" +
    "					</div>\n" +
    "					\n" +
    "					\n" +
    "					<div class=\"row\">\n" +
    "						<label for=\"antibanding_select\" class=\"col-xs-3\">Antibanding</label>\n" +
    "						<div class=\"col-md-5 col-xs-7\">\n" +
    "			              <div class=\"input-group\">\n" +
    "				       	 	<select class=\"form-control\" ng-model=\"antibanding\"\n" +
    "									ng-options=\"a as a for a in antibandings\"\n" +
    "									ng-change=\"selectAntibanding\"></select>\n" +
    "			              </div>\n" +
    "			            </div>\n" +
    "					</div>\n" +
    "					\n" +
    "			</div>\n" +
    "		</form>\n" +
    "		\n" +
    "		<br />\n" +
    "\n" +
    "		<h4 class=\"panel-title\">\n" +
    "            <a data-toggle=\"collapse\" id=\"video_settings\" href ng-click=\"selectVideoSettings()\">\n" +
    "              Video Control <b class=\"caret\"></b>\n" +
    "            </a>\n" +
    "        </h4>\n" +
    "\n" +
    "		<div class=\"row-fluid checkbox\" ng-show=\"videoSettings\">		\n" +
    "			<br/>\n" +
    "			<div class=\"input-group col-md-8 col-xs-12\">\n" +
    "				<span class=\"input-group-addon glyphicon glyphicon-tag\"></span>\n" +
    "				<input id=\"video_tag\" ng-model=\"videoPrefix\" type=\"text\" class=\"form-control form-control-inline\" placeholder=\"Enter a video prefix for this recording\">\n" +
    "				<span class=\"input-group-btn\">\n" +
    "				   <button id=\"rec_button\" type=\"button\" class=\"btn btn-default\" uib-tooltip=\"Manual recording\"\n" +
    "					uib-tooltip-placement=\"top\" ng-click=\"startRecording()\">\n" +
    "					<i class='glyphicon glyphicon-record'></i> Record\n" +
    "				   </button>\n" +
    "				</span>\n" +
    "			  </div>\n" +
    "			  <br/>\n" +
    "         </div>\n" +
    "\n" +
    "		<div class=\"row\" ng-show=\"recordingStatus.isRecording == 'true'\">\n" +
    "			<br/>\n" +
    "			<div class=\"col-md-8 col-xs-12 text-center\"><img src=\"assets/images/record.gif\">&nbsp;&nbsp;Recording to\n" +
    "			  	<span id=\"rec_location\">{{recordingStatus.fileName}}</span> \n" +
    "			</div>\n" +
    "			<div class=\"col-md-8 col-xs-12 text-center\"><br/>\n" +
    "				  <button id=\"rec_stop\" class=\"btn btn-default\" type=\"button\" ng-click=\"stopRecording()\">\n" +
    "					<i class=\"glyphicon glyphicon-stop\"></i>&nbsp;&nbsp;Stop\n" +
    "				  </button>\n" +
    "			 </div>			 \n" +
    "		</div>\n" +
    "			\n" +
    "		<br/>\n" +
    "		<h4 class=\"panel-title\">\n" +
    "            <a data-toggle=\"collapse\" id=\"nightvision_settings\" href ng-click=\"selectNightVisionSettings()\">\n" +
    "              Night Vision <b class=\"caret\"></b>\n" +
    "            </a>\n" +
    "        </h4>\n" +
    "        \n" +
    "\n" +
    "        <div class=\"row-fluid\" ng-show=\"nightVisionSettings\">	        \n" +
    "	        <form class=\"form-horizontal\">	              		\n" +
    "					<div class=\"panel panel-default noborder\">	  \n" +
    "						  <div class=\"panel-body\">\n" +
    "						  		<div class=\"row cols-xs-12 col-md-6\">	  \n" +
    "							    	<ul class=\"list-group\">\n" +
    "					                    <li class=\"list-group-item noborder\">\n" +
    "					                        <label for=\"nightVision\">{{nightVisionStatus}}</label>\n" +
    "					                        <div class=\"material-switch pull-right\">\n" +
    "					                             <input id=\"nightVisionSwitch\" name=\"nightVisionSwitch\" ng-model=\"nightVisionSwitch\" type=\"checkbox\" ng-click=\"selectNightVision($event)\"/>\n" +
    "				                            	 <label for=\"nightVisionSwitch\" class=\"label-info\"></label>\n" +
    "					                        </div>\n" +
    "					                    </li>\n" +
    "\n" +
    "					                   <li class=\"list-group-item noborder\">\n" +
    "					                    	<label class=\"checkbox-inline\">\n" +
    "												<input type=\"checkbox\" id=\"checkbox_histogram\" ng-model=\"histogram\" ng-disabled=\"!nightVisionSwitch\" ng-click=\"enableHistogramEqualization($event)\">\n" +
    "									          		 <select ng-model=\"histogramEqualizationOption\" ng-disabled=\"!nightVisionSwitch\" ng-change=\"selectHistogramEqualization()\">\n" +
    "									                	<option>Default</option>\n" +
    "									                	<option>Color</option>\n" +
    "									                </select>\n" +
    "									                &nbsp;&nbsp;&nbsp;&nbsp;\n" +
    "											</label>\n" +
    "										\n" +
    "											<label class=\"checkbox-inline\">\n" +
    "												<input type=\"checkbox\" id=\"checkbox_gamma\" ng-model=\"gamma\" ng-disabled=\"!nightVisionSwitch\"\n" +
    "													ng-click=\"enableGammaCorrection($event)\" uib-tooltip-placement=\"top\" uib-tooltip=\"Use gamma correction\">Gamma correction\n" +
    "											</label>\n" +
    "\n" +
    "					                    </li>	\n" +
    "\n" +
    "					                    <li class=\"list-group-item noborder\">\n" +
    "					                        <label for=\"range_gamma_level\" class=\"col-xs-3\">Gamma level</label>\n" +
    "											<div class=\"col-xs-7\">\n" +
    "												<input id=\"range_gamma_level\" class=\"col-xs-6 col-md-4\" type=\"range\"\n" +
    "													min=\"1\" max=\"20\" step=\"1\" ng-model=\"gammaLevel\" ng-disabled=\"!gamma\"\n" +
    "													value=\"{{gammaLevel}}\" ng-change=\"selectGammaLevel()\"  ng-model-options=\"{ debounce: 500 }\">\n" +
    "											</div>\n" +
    "											<label for=\"range_gamma_level\" id=\"range_gamma_level_label\" class=\"control-label col-xs-2\">{{gammaLevel}}</label>					                    \n" +
    "										</li>\n" +
    "										\n" +
    "\n" +
    "				                    </ul>\n" +
    "						 		</div>\n" +
    "						</div>\n" +
    "		         </div>		         \n" +
    "	         </form>\n" +
    "        </div>\n" +
    "\n" +
    "        <br/>\n" +
    "\n" +
    "		<div class=\"row-fluid\">\n" +
    "			<div class=\"radio\">\n" +
    "				<label>\n" +
    "					<input type=\"radio\" ng-model=\"streamingMethod\" value=\"js\" ng-disabled=\"inStreaming\">Periodic camera snapshot (supported on all platforms) \n" +
    "				</label>\n" +
    "			</div>\n" +
    "\n" +
    "			<div class=\"radio\">\n" +
    "				<label>\n" +
    "					<input type=\"radio\" ng-model=\"streamingMethod\" value=\"mjpg\" ng-disabled=\"inStreaming\">Use motion JPEG (supported on Chrome and Firefox)\n" +
    "				</label>\n" +
    "			</div>	\n" +
    "		</div>	\n" +
    "		\n" +
    "		\n" +
    "		<div class=\"row\">\n" +
    "\n" +
    "			<!-- <div class=\"radio\">\n" +
    "				<label>\n" +
    "					<input type=\"checkbox\" id=\"checkbox-lock\" ng-model=\"enableCameraLock\" ng-click=\"lockCamera($event)\"> Lock camera screen after turning on\n" +
    "				</label>\n" +
    "			</div> -->\n" +
    "			\n" +
    "			<div class=\"radio\">\n" +
    "				<label>\n" +
    "					<input type=\"checkbox\" id=\"checkbox-shutdown\"\n" +
    "						ng-model=\"enableCameraShutdown\" ng-click=\"shutdownCamera($event)\"> Shutdown\n" +
    "					camera when turning off to save battery (phone must be unlocked)\n" +
    "				</label>\n" +
    "			</div>\n" +
    "		\n" +
    "		</div>\n" +
    "\n" +
    "		<p>\n" +
    "		<br/>\n" +
    "		<div ng-show=\"mediaInfo\" class=\"alert alert-info\">\n" +
    "			<h4>Media Information</h4>\n" +
    "			Motion JPEG: {{ surveillanceUrl + '/video/live.mjpg'}} <br/>\n" +
    "			Video Source: {{videoSrc.path}}<br /> Audio Source: {{audioSrc}} <br /> <br />			\n" +
    "		</div>\n" +
    "		</p>\n" +
    "\n" +
    "	</div>\n" +
    "</form>");
}]);

angular.module("tracker/tracker.tpl.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("tracker/tracker.tpl.html",
    "<div class=\"marketing\">\n" +
    "  <div class=\"row\">\n" +
    "    <div class=\"col-xs-12 col-sm-6 col-md-4\">\n" +
    "      <a href ui-sref=\"device\"><h4><i class=\"fa fa-tablet\"></i> My Devices</h4></a>\n" +
    "      <p>\n" +
    "        Monitor connected devices and cameras.\n" +
    "      </p>\n" +
    "    </div>\n" +
    "	\n" +
    "	<div class=\"col-xs-12 col-sm-6 col-md-4\">\n" +
    "      <a href ui-sref=\"notif\"><h4><i class=\"fa fa-comment-o\"></i> Notifications</h4></a>\n" +
    "      <p>\n" +
    "       View received notifications.\n" +
    "      </p>\n" +
    "    </div>    \n" +
    "\n" +
    "  </div>\n" +
    "  \n" +
    "\n" +
    "</div>\n" +
    "\n" +
    "");
}]);

angular.module("ussd/ussd.tpl.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("ussd/ussd.tpl.html",
    "<div class=\"container-fluid\">\n" +
    "	<div class=\"row\">\n" +
    "		<div class=\"col-md-3\" id=\"sidebar\" ng-include=\"'assets/view/services_sidebar.html'\"></div>\n" +
    "		\n" +
    "		\n" +
    "		<div class=\"col-md-9\" id=\"content\">\n" +
    "			\n" +
    "			<div class=\"alert alert-info\">\n" +
    "				<h3 class=\"alert-heading\">USSD API</h3>\n" +
    "			</div>\n" +
    "			\n" +
    "			<div class=\"alert alert-warning\">\n" +
    "			<strong>\n" +
    "				In order for USSD APIs to work, myMobKit accessibility service must be turned on under Settings->Accessibility.\n" +
    "			</strong>\n" +
    "			</div>\n" +
    "			\n" +
    "			<br/>\n" +
    "			\n" +
    "			<div class=\"alert alert-success\">				\n" +
    "				<h4 class=\"alert-heading\"> <button class=\"btn btn-default btn-xs\">POST</button> Send USSD </h4>\n" +
    "				<br/>\n" +
    "				<p class=\"long-wrap\">\n" +
    "				{{ussdLink}}	\n" +
    "				</p>\n" +
    "			</div>\n" +
    "			\n" +
    "			<table id=\"sms_api_doc\" class=\"table table-striped\">\n" +
    "				<thead>\n" +
    "					<tr>\n" +
    "						<th>Parameter</th>\n" +
    "						<th>Description</th>\n" +
    "					</tr>\n" +
    "				</thead>\n" +
    "				\n" +
    "				<tbody>    	      \n" +
    "					<tr id=\"Command\">\n" +
    "						<td>\n" +
    "							Command\n" +
    "						</td>\n" +
    "						<td>\n" +
    "							USSD command to send. E.g. *123#.\n" +
    "						</td>\n" +
    "					</tr>\n" +
    "					\n" +
    "					<tr id=\"To\">\n" +
    "						<td>\n" +
    "							Pattern\n" +
    "						</td>\n" +
    "						<td>\n" +
    "							Expected response pattern from the command. Provide this value if the response pattern is consistent, e.g. \n" +
    "							if the response always starts with \"Your current balance is ...\".\n" +
    "						</td>\n" +
    "					</tr>\n" +
    "					\n" +
    "				</tbody>\n" +
    "			</table>\n" +
    "			\n" +
    "			\n" +
    "			<div class=\"alert alert-warning\">\n" +
    "				<h4 class=\"alert-heading\">Response</h4>\n" +
    "				<p class=\"long-wrap\">\n" +
    "				<code>\n" +
    "				\n" +
    "				{\n" +
    "				    \"responsePattern\": \"\",\n" +
    "				    \"sessionId\": \"329797043932645619\",\n" +
    "				    \"ussdCommand\": \"*128%23\",\n" +
    "				</code><br/><code>\n" +
    "				    \"description\": \"\",\n" +
    "				    \"requestMethod\": \"POST\",\n" +
    "				    \"isSuccessful\": true\n" +
    "				}\n" +
    "				</code>\n" +
    "				</p>\n" +
    "			</div>\n" +
    "			\n" +
    "			<table id=\"sms_api_doc\" class=\"table table-striped\">\n" +
    "			<thead>\n" +
    "				<tr>\n" +
    "					<th>Field</th>\n" +
    "					<th>Description</th>\n" +
    "				</tr>\n" +
    "			</thead>\n" +
    "			\n" +
    "			<tbody>    	      \n" +
    "				<tr id=\"responsePattern\">\n" +
    "					<td>\n" +
    "						responsePattern\n" +
    "					</td>\n" +
    "					\n" +
    "				<td>\n" +
    "					The expected response pattern.	\n" +
    "				</td>\n" +
    "				</tr>\n" +
    "				\n" +
    "				<tr id=\"sessionId\">\n" +
    "					<td>\n" +
    "						sessionId\n" +
    "					</td>\n" +
    "					\n" +
    "				<td>\n" +
    "					Session id which will be used to query the response later.\n" +
    "				</td>\n" +
    "				</tr>\n" +
    "				\n" +
    "				<tr id=\"ussdCommand\">\n" +
    "					<td>\n" +
    "						ussdCommand\n" +
    "					</td>\n" +
    "					\n" +
    "				<td>\n" +
    "					The USSD command.\n" +
    "				</td>\n" +
    "				</tr>\n" +
    "				\n" +
    "				\n" +
    "				<tr id=\"description\">\n" +
    "					<td>\n" +
    "						description\n" +
    "					</td>\n" +
    "				<td>\n" +
    "					Contains the error message if isSuccessful is \"false\".\n" +
    "				</td>\n" +
    "				</tr>\n" +
    "				\n" +
    "				<tr id=\"requestMethod\">\n" +
    "					<td>\n" +
    "						requestMethod\n" +
    "					</td>\n" +
    "				<td>\n" +
    "					Request method. Default to \"GET\", \"POST\" or \"DELETE\" depending on the HTTP method.\n" +
    "				</td>\n" +
    "				</tr>\n" +
    "				\n" +
    "				<tr id=\"requestMethod\">\n" +
    "					<td>\n" +
    "						isSuccessful\n" +
    "					</td>\n" +
    "				<td>\n" +
    "					true if operation is successful. Otherwise returns false.\n" +
    "				</td>\n" +
    "				</tr>\n" +
    "				\n" +
    "				\n" +
    "				\n" +
    "			</tbody>\n" +
    "			</table>\n" +
    "			\n" +
    "			\n" +
    "			<div class=\"alert alert-success\">\n" +
    "				<h4 class=\"alert-heading\"> <button class=\"btn btn-default btn-xs\">GET</button> Get USSD Response </h4>\n" +
    "				<br/>\n" +
    "				<p class=\"long-wrap\">\n" +
    "				{{ussdLink}}<b>{session id}</b>\n" +
    "				</p>	\n" +
    "			</div>\n" +
    "			\n" +
    "			<table id=\"sms_api_doc\" class=\"table table-striped\">\n" +
    "				<thead>\n" +
    "					<tr>\n" +
    "						<th>Parameter</th>\n" +
    "						<th>Description</th>\n" +
    "					</tr>\n" +
    "				</thead>\n" +
    "				\n" +
    "				<tbody>    	      \n" +
    "					<tr id=\"SessionId\">\n" +
    "						<td>\n" +
    "							Session Id\n" +
    "						</td>\n" +
    "						<td>\n" +
    "							Session id from the POST command.\n" +
    "						</td>\n" +
    "					</tr>\n" +
    "					\n" +
    "				</tbody>\n" +
    "			</table>\n" +
    "			\n" +
    "			\n" +
    "			<div class=\"alert alert-warning\">\n" +
    "				<h4 class=\"alert-heading\">Response</h4>\n" +
    "				<p class=\"long-wrap\">\n" +
    "				<code>\n" +
    "				{\n" +
    "				    \"response\": \"[DiGi Menu\\n[All prices quoted here are excluding GST]\\n1 My Account\\n2 Free Pass</code><br/><code>\n" +
    "				    \\n3 Internet\\n4 Music\\n5 Promotions\\n6 Facebook & Friends\\n7 TV & Games\\n99 More, Cancel, Send]\",</code><br/><code>\"\n" +
    "				    \"sessionId\": \"8003226424663755025\",\n" +
    "				    \"description\": \"\",\n" +
    "				    \"requestMethod\": \"GET\",\n" +
    "				    \"isSuccessful\": true\n" +
    "				}\n" +
    "				</code>\n" +
    "				</p>\n" +
    "			</div>\n" +
    "			\n" +
    "				<table id=\"sms_api_doc\" class=\"table table-striped\">\n" +
    "			<thead>\n" +
    "				<tr>\n" +
    "					<th>Field</th>\n" +
    "					<th>Description</th>\n" +
    "				</tr>\n" +
    "			</thead>\n" +
    "			\n" +
    "			<tbody>    	      \n" +
    "				<tr id=\"response\">\n" +
    "					<td>\n" +
    "						response\n" +
    "					</td>\n" +
    "					\n" +
    "				<td>\n" +
    "					Response for the USSD command\n" +
    "				</td>\n" +
    "				</tr>\n" +
    "				\n" +
    "				<tr id=\"sessionId\">\n" +
    "					<td>\n" +
    "						sessionId\n" +
    "					</td>\n" +
    "					\n" +
    "				<td>\n" +
    "					Session id\n" +
    "				</td>\n" +
    "				</tr>\n" +
    "				\n" +
    "			</tbody>\n" +
    "			</table>\n" +
    "			\n" +
    "		\n" +
    "		</div>\n" +
    "		\n" +
    "	</div>\n" +
    "	\n" +
    "	\n" +
    "</div>\n" +
    "					\n" +
    "	\n" +
    "		\n" +
    "		\n" +
    "	\n" +
    "");
}]);

angular.module("vcalendar/vcalendar.tpl.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("vcalendar/vcalendar.tpl.html",
    "<div class=\"container-fluid\">\n" +
    "	<div class=\"row\">\n" +
    "		<div class=\"col-md-3\" id=\"sidebar\" ng-include=\"'assets/view/services_sidebar.html'\"></div>\n" +
    "\n" +
    "		<div class=\"col-md-9\" id=\"content\">\n" +
    "		\n" +
    "			<div class=\"alert alert-info\">\n" +
    "				<h3 class=\"alert-heading\">vCalendar API</h3>\n" +
    "			</div>	\n" +
    "\n" +
    "\n" +
    "			<div class=\"alert alert-success\">\n" +
    "				<h4 class=\"alert-heading\"> <button class=\"btn btn-default btn-xs\">POST</button> Send vCalendar </h4> \n" +
    "				<p></p> <br>\n" +
    "				<p class=\"long-wrap\">\n" +
    "				{{vCalendarLink}}\n" +
    "				</p>\n" +
    "			</div>\n" +
    "			\n" +
    "			<table id=\"sms_api_doc\" class=\"table table-striped\">\n" +
    "				<thead>\n" +
    "					<tr>\n" +
    "						<th>Parameter</th>\n" +
    "						<th>Description</th>\n" +
    "					</tr>\n" +
    "				</thead>\n" +
    "				\n" +
    "				<tbody>    	      \n" +
    "					<tr id=\"To\">\n" +
    "						<td>\n" +
    "							to\n" +
    "						</td>\n" +
    "						<td>\n" +
    "						The destination number or contact name. Mandatory parameter.\n" +
    "						</td>\n" +
    "					</tr>\n" +
    "\n" +
    "					<tr id=\"CC\">\n" +
    "						<td>\n" +
    "							cc\n" +
    "						</td>\n" +
    "						<td>\n" +
    "						CC destination number or contact name.\n" +
    "						</td>\n" +
    "					</tr>\n" +
    "\n" +
    "					<tr id=\"BCC\">\n" +
    "						<td>\n" +
    "							bcc\n" +
    "						</td>\n" +
    "						<td>\n" +
    "						BCC destination number or contact name.\n" +
    "						</td>\n" +
    "					</tr>\n" +
    "					\n" +
    "					<tr id=\"Subject\">\n" +
    "						<td>\n" +
    "							subject\n" +
    "						</td>\n" +
    "						<td>\n" +
    "							vCalendar message subject.\n" +
    "						</td>\n" +
    "					</tr>\n" +
    "\n" +
    "					<tr id=\"Body\">\n" +
    "						<td>\n" +
    "							body\n" +
    "						</td>\n" +
    "						<td>\n" +
    "							vCalendar message body.\n" +
    "						</td>\n" +
    "					</tr>\n" +
    "\n" +
    "					<tr id=\"DeliveryReport\">\n" +
    "						<td>\n" +
    "							deliveryReport\n" +
    "						</td>\n" +
    "						<td>\n" +
    "							0 for no delivery report, any other values indicate delivery report is required. Default to 0 for no delivery report.\n" +
    "						</td>\n" +
    "					</tr>\n" +
    "\n" +
    "					<tr id=\"ReadReport\">\n" +
    "						<td>\n" +
    "							readReport\n" +
    "						</td>\n" +
    "						<td>\n" +
    "							0 for no read report, any other values indicate read report is required. Default to 0 for no read report.\n" +
    "						</td>\n" +
    "					</tr>\n" +
    "					\n" +
    "					<tr>\n" +
    "						<td>\n" +
    "							vCalendar\n" +
    "						</td>\n" +
    "						<td>\n" +
    "							vCalendar content.\n" +
    "						</td>\n" +
    "					</tr>					\n" +
    "					\n" +
    "				</tbody>\n" +
    "			</table>\n" +
    "	\n" +
    "			\n" +
    "			<div class=\"alert alert-warning\">\n" +
    "				<h4 class=\"alert-heading\">Response</h4>\n" +
    "				<p class=\"long-wrap\">\n" +
    "				<code>			\n" +
    "\n" +
    "					{\n" +
    "					    \"vCalendar\": {\n" +
    "					        \"bcc\": \"\",\n" +
    "					        \"body\": \"msg body\",\n" +
    "					</code><br/><code>\n" +
    "					        \"cc\": \"\",\n" +
    "					        \"date\": \"2016-03-20T21:59:45GMT+08:00\",\n" +
    "					</code><br/><code>\n" +
    "					        \"to\": \"8632323232323\",\n" +
    "					        \"subject\": \"My vCalendar\",\n" +
    "					        \"id\": \"6492044552370211736\",\n" +
    "					</code><br/><code>\n" +
    "					        \"isDelivered\": false,\n" +
    "					        \"isRead\": false,\n" +
    "					        \"readReport\": false,\n" +
    "					</code><br/><code>\n" +
    "					        \"deliveryReport\": false\n" +
    "					    },\n" +
    "					    \"description\": \"\",\n" +
    "					    \"requestMethod\": \"POST\",\n" +
    "					</code><br/><code>\n" +
    "					    \"isSuccessful\": true\n" +
    "					}\n" +
    "				</code>\n" +
    "				</p>\n" +
    "			</div>\n" +
    "			\n" +
    "			\n" +
    "			<table id=\"mms_api_doc\" class=\"table table-striped\">\n" +
    "			<thead>\n" +
    "				<tr>\n" +
    "					<th>Field</th>\n" +
    "					<th>Description</th>\n" +
    "				</tr>\n" +
    "			</thead>\n" +
    "			\n" +
    "			<tbody>    	    \n" +
    "\n" +
    "				<tr id=\"message\">\n" +
    "					<td>\n" +
    "						vCalendar\n" +
    "					</td>\n" +
    "					\n" +
    "				<td>\n" +
    "					\n" +
    "					vCalendar object containing the following fields for the sent message.\n" +
    "					<br/><br/>\n" +
    "					\n" +
    "					<dl class=\"dl-horizontal\">\n" +
    "					  <dt>to</dt>\n" +
    "					  <dd>To number.</dd>\n" +
    "					  <dt>cc</dt>\n" +
    "					  <dd>CC number.</dd>\n" +
    "					  <dt>bcc</dt>\n" +
    "					  <dd>BCC number</dd>\n" +
    "					  <dt>id</dt>\n" +
    "					  <dd>A <b>unique identifier</b> that can be used to check the message status.</dd>\n" +
    "					  <dt>subject</dt>\n" +
    "					  <dd>Message subject</dd>		\n" +
    "					  <dt>body</dt>\n" +
    "					  <dd>Message body</dd>			\n" +
    "					  <dt>deliveryReport</dt>\n" +
    "					  <dd>Flag to indicates if delivery report is required.</dd>	\n" +
    "					  <dt>readReport</dt>\n" +
    "					  <dd>Flag to indicates if read report is required.</dd>	\n" +
    "					  <dt>isDelivered</dt>\n" +
    "					  <dd>Flag to indicates if message is delivered.</dd>	\n" +
    "					  <dt>isRead</dt>\n" +
    "					  <dd>Flag to indicates if message is read.</dd>	\n" +
    "					  <dt>date</dt>\n" +
    "					  <dd>Message sent date.</dd>				  \n" +
    "					</dl>\n" +
    "				</td>\n" +
    "\n" +
    "				<tr id=\"description\">\n" +
    "					<td>\n" +
    "						description\n" +
    "					</td>\n" +
    "				<td>\n" +
    "					Contains the error message if isSuccessful is \"false\".\n" +
    "				</td>\n" +
    "				</tr>\n" +
    "				\n" +
    "				<tr id=\"requestMethod\">\n" +
    "					<td>\n" +
    "						requestMethod\n" +
    "					</td>\n" +
    "				<td>\n" +
    "					Request method. Default to \"GET\", \"POST\" or \"DELETE\" depending on the HTTP method.\n" +
    "				</td>\n" +
    "				</tr>\n" +
    "				\n" +
    "				<tr id=\"requestMethod\">\n" +
    "					<td>\n" +
    "						isSuccessful\n" +
    "					</td>\n" +
    "				<td>\n" +
    "					true if operation is successful. Otherwise returns false.\n" +
    "				</td>\n" +
    "				</tr>\n" +
    "			</tbody>\n" +
    "			</table>			\n" +
    "\n" +
    "\n" +
    "\n" +
    "			<div class=\"alert alert-default\">\n" +
    "				<h4 id=\"contentapi\" class=\"alert-heading\"> <span class=\"label label-default\">Note</span>\n" +
    "				</h4> \n" +
    "				<br/>\n" +
    "				<p class=\"long-wrap\">\n" +
    "				\n" +
    "				<div class=\"list-group\">\n" +
    "				  <a href=\"#\" class=\"list-group-item\">\n" +
    "				    <h4 class=\"list-group-item-heading\">Check vCalendar Message Status</h4>\n" +
    "				    <p class=\"list-group-item-text\">The vCalendar message is actually a MMS message. Use the MMS API to check the message status.</p>\n" +
    "				  </a>				  \n" +
    "				</div>\n" +
    "\n" +
    "				</p>\n" +
    "			</div>\n" +
    "			\n" +
    "		\n" +
    "			<div class=\"alert alert-info row\">\n" +
    "				<p>\n" +
    "				<h4 class=\"alert-heading\">Usage</h4>\n" +
    "				</p> <br/>	\n" +
    "					\n" +
    "				<form class=\"form-horizontal\" ng-submit=\"sendvCalendarMessage()\">\n" +
    "				\n" +
    "				  <div class=\"form-group\">\n" +
    "					<label for=\"to\" class=\"col-sm-2 control-label\">To</label>\n" +
    "					<div class=\"col-sm-10\">\n" +
    "						<input type=\"text\" class=\"form-control\" ng-model=\"mmsMessage.to\" required=\"true\" id=\"to\" placeholder=\"To\"/>\n" +
    "					</div>\n" +
    "				  </div>\n" +
    "				  \n" +
    "				  <div class=\"form-group\">\n" +
    "					<label for=\"subject\" class=\"col-sm-2 control-label\">Subject</label>\n" +
    "					<div class=\"col-sm-10\">\n" +
    "	 			  		<input type=\"text\" class=\"form-control\" ng-model=\"mmsMessage.subject\" required=\"true\" id=\"subject\" placeholder=\"Subject\"/>\n" +
    "	 			  	</div>\n" +
    "				  </div>\n" +
    "\n" +
    "				  <div class=\"form-group\">\n" +
    "					<label for=\"text\" class=\"col-sm-2 control-label\">Body</label>	\n" +
    "					<div class=\"col-sm-10\">			\n" +
    "						<input type=\"text\" class=\"form-control\" ng-model=\"mmsMessage.body\" required=\"true\" id=\"text\" placeholder=\"Text\" />\n" +
    "					</div>\n" +
    "				  </div>\n" +
    "				\n" +
    "				  <div class=\"form-group\">\n" +
    "					<label for=\"text\" class=\"col-sm-2 control-label\">vCalendar</label>	\n" +
    "					<div class=\"col-sm-10\">			\n" +
    "						<textarea id=\"vcalendar\" cols=\"40\" rows=\"5\" ng-model=\"mmsMessage.vcalendar\" required=\"true\">					    	\n" +
    "					    </textarea>\n" +
    "					</div>\n" +
    "				  </div>\n" +
    "			\n" +
    "				 <div class=\"form-group\">\n" +
    "				  	<div class=\"col-sm-offset-2 col-sm-10\">\n" +
    "				  		<br/>\n" +
    "					  <button type=\"submit\" class=\"btn btn-default\" ng-disabled=\"mmsMessageStatus\">Send vCalendar</button>\n" +
    "					  <br/>\n" +
    "					  <br/>\n" +
    "					  {{mmsSendStatus}}\n" +
    "					</div>\n" +
    "				  </div>						\n" +
    "				</form>\n" +
    "\n" +
    "			</div>	\n" +
    "		</div>\n" +
    "		\n" +
    "		</div>\n" +
    "	</div>	\n" +
    "	\n" +
    "</div>\n" +
    "					\n" +
    "	\n" +
    "		\n" +
    "		\n" +
    "	\n" +
    "");
}]);

angular.module("vcard/vcard.tpl.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("vcard/vcard.tpl.html",
    "<div class=\"container-fluid\">\n" +
    "	<div class=\"row\">\n" +
    "		<div class=\"col-md-3\" id=\"sidebar\" ng-include=\"'assets/view/services_sidebar.html'\"></div>\n" +
    "\n" +
    "		<div class=\"col-md-9\" id=\"content\">\n" +
    "		\n" +
    "			<div class=\"alert alert-info\">\n" +
    "				<h3 class=\"alert-heading\">vCard API</h3>\n" +
    "			</div>	\n" +
    "\n" +
    "\n" +
    "			<div class=\"alert alert-success\">\n" +
    "				<h4 class=\"alert-heading\"> <button class=\"btn btn-default btn-xs\">POST</button> Send vCard </h4> \n" +
    "				<p></p> <br>\n" +
    "				<p class=\"long-wrap\">\n" +
    "				{{vCardLink}}\n" +
    "				</p>\n" +
    "			</div>\n" +
    "			\n" +
    "			<table id=\"sms_api_doc\" class=\"table table-striped\">\n" +
    "				<thead>\n" +
    "					<tr>\n" +
    "						<th>Parameter</th>\n" +
    "						<th>Description</th>\n" +
    "					</tr>\n" +
    "				</thead>\n" +
    "				\n" +
    "				<tbody>    	      \n" +
    "					<tr id=\"To\">\n" +
    "						<td>\n" +
    "							to\n" +
    "						</td>\n" +
    "						<td>\n" +
    "						The destination number or contact name. Mandatory parameter.\n" +
    "						</td>\n" +
    "					</tr>\n" +
    "\n" +
    "					<tr id=\"CC\">\n" +
    "						<td>\n" +
    "							cc\n" +
    "						</td>\n" +
    "						<td>\n" +
    "						CC destination number or contact name.\n" +
    "						</td>\n" +
    "					</tr>\n" +
    "\n" +
    "					<tr id=\"BCC\">\n" +
    "						<td>\n" +
    "							bcc\n" +
    "						</td>\n" +
    "						<td>\n" +
    "						BCC destination number or contact name.\n" +
    "						</td>\n" +
    "					</tr>\n" +
    "					\n" +
    "					<tr id=\"Subject\">\n" +
    "						<td>\n" +
    "							subject\n" +
    "						</td>\n" +
    "						<td>\n" +
    "							vCard message subject.\n" +
    "						</td>\n" +
    "					</tr>\n" +
    "\n" +
    "					<tr id=\"Body\">\n" +
    "						<td>\n" +
    "							body\n" +
    "						</td>\n" +
    "						<td>\n" +
    "							vCard message body.\n" +
    "						</td>\n" +
    "					</tr>\n" +
    "\n" +
    "					<tr id=\"DeliveryReport\">\n" +
    "						<td>\n" +
    "							deliveryReport\n" +
    "						</td>\n" +
    "						<td>\n" +
    "							0 for no delivery report, any other values indicate delivery report is required. Default to 0 for no delivery report.\n" +
    "						</td>\n" +
    "					</tr>\n" +
    "\n" +
    "					<tr id=\"ReadReport\">\n" +
    "						<td>\n" +
    "							readReport\n" +
    "						</td>\n" +
    "						<td>\n" +
    "							0 for no read report, any other values indicate read report is required. Default to 0 for no read report.\n" +
    "						</td>\n" +
    "					</tr>\n" +
    "					\n" +
    "					<tr>\n" +
    "						<td>\n" +
    "							vCard\n" +
    "						</td>\n" +
    "						<td>\n" +
    "							vCard content.\n" +
    "						</td>\n" +
    "					</tr>					\n" +
    "					\n" +
    "				</tbody>\n" +
    "			</table>\n" +
    "	\n" +
    "			\n" +
    "			<div class=\"alert alert-warning\">\n" +
    "				<h4 class=\"alert-heading\">Response</h4>\n" +
    "				<p class=\"long-wrap\">\n" +
    "				<code>			\n" +
    "\n" +
    "					{\n" +
    "					    \"vCard\": {\n" +
    "					        \"bcc\": \"\",\n" +
    "					        \"body\": \"msg body\",\n" +
    "					</code><br/><code>\n" +
    "					        \"cc\": \"\",\n" +
    "					        \"date\": \"2016-03-20T21:59:45GMT+08:00\",\n" +
    "					</code><br/><code>\n" +
    "					        \"to\": \"8632323232323\",\n" +
    "					        \"subject\": \"My vCard\",\n" +
    "					        \"id\": \"6492044552370211736\",\n" +
    "					</code><br/><code>\n" +
    "					        \"isDelivered\": false,\n" +
    "					        \"isRead\": false,\n" +
    "					        \"readReport\": false,\n" +
    "					</code><br/><code>\n" +
    "					        \"deliveryReport\": false\n" +
    "					    },\n" +
    "					    \"description\": \"\",\n" +
    "					    \"requestMethod\": \"POST\",\n" +
    "					</code><br/><code>\n" +
    "					    \"isSuccessful\": true\n" +
    "					}\n" +
    "				</code>\n" +
    "				</p>\n" +
    "			</div>\n" +
    "			\n" +
    "			\n" +
    "			<table id=\"mms_api_doc\" class=\"table table-striped\">\n" +
    "			<thead>\n" +
    "				<tr>\n" +
    "					<th>Field</th>\n" +
    "					<th>Description</th>\n" +
    "				</tr>\n" +
    "			</thead>\n" +
    "			\n" +
    "			<tbody>    	    \n" +
    "\n" +
    "				<tr id=\"message\">\n" +
    "					<td>\n" +
    "						vCard\n" +
    "					</td>\n" +
    "					\n" +
    "				<td>					\n" +
    "					vCard object containing the following fields for the sent message.\n" +
    "					<br/><br/>\n" +
    "					\n" +
    "					<dl class=\"dl-horizontal\">\n" +
    "					  <dt>to</dt>\n" +
    "					  <dd>To number.</dd>\n" +
    "					  <dt>cc</dt>\n" +
    "					  <dd>CC number.</dd>\n" +
    "					  <dt>bcc</dt>\n" +
    "					  <dd>BCC number</dd>\n" +
    "					  <dt>id</dt>\n" +
    "					  <dd>A <b>unique identifier</b> that can be used to check the message status.</dd>\n" +
    "					  <dt>subject</dt>\n" +
    "					  <dd>Message subject</dd>		\n" +
    "					  <dt>body</dt>\n" +
    "					  <dd>Message body</dd>			\n" +
    "					  <dt>deliveryReport</dt>\n" +
    "					  <dd>Flag to indicates if delivery report is required.</dd>	\n" +
    "					  <dt>readReport</dt>\n" +
    "					  <dd>Flag to indicates if read report is required.</dd>	\n" +
    "					  <dt>isDelivered</dt>\n" +
    "					  <dd>Flag to indicates if message is delivered.</dd>	\n" +
    "					  <dt>isRead</dt>\n" +
    "					  <dd>Flag to indicates if message is read.</dd>	\n" +
    "					  <dt>date</dt>\n" +
    "					  <dd>Message sent date.</dd>				  \n" +
    "					</dl>\n" +
    "				</td>\n" +
    "\n" +
    "				<tr id=\"description\">\n" +
    "					<td>\n" +
    "						description\n" +
    "					</td>\n" +
    "				<td>\n" +
    "					Contains the error message if isSuccessful is \"false\".\n" +
    "				</td>\n" +
    "				</tr>\n" +
    "				\n" +
    "				<tr id=\"requestMethod\">\n" +
    "					<td>\n" +
    "						requestMethod\n" +
    "					</td>\n" +
    "				<td>\n" +
    "					Request method. Default to \"GET\", \"POST\" or \"DELETE\" depending on the HTTP method.\n" +
    "				</td>\n" +
    "				</tr>\n" +
    "				\n" +
    "				<tr id=\"requestMethod\">\n" +
    "					<td>\n" +
    "						isSuccessful\n" +
    "					</td>\n" +
    "				<td>\n" +
    "					true if operation is successful. Otherwise returns false.\n" +
    "				</td>\n" +
    "				</tr>\n" +
    "\n" +
    "			</tbody>\n" +
    "			</table>			\n" +
    "\n" +
    "\n" +
    "\n" +
    "			<div class=\"alert alert-default\">\n" +
    "				<h4 id=\"contentapi\" class=\"alert-heading\"> <span class=\"label label-default\">Note</span>\n" +
    "				</h4> \n" +
    "				<br/>\n" +
    "				<p class=\"long-wrap\">\n" +
    "				\n" +
    "				<div class=\"list-group\">\n" +
    "				  <a href=\"#\" class=\"list-group-item\">\n" +
    "				    <h4 class=\"list-group-item-heading\">Generate vCard</h4>\n" +
    "				    <p class=\"list-group-item-text\">Use the Contact API to get the vCard for stored contacts.</p>\n" +
    "				  </a>\n" +
    "				  <a href=\"#\" class=\"list-group-item\">\n" +
    "				    <h4 class=\"list-group-item-heading\">Check vCard Message Status</h4>\n" +
    "				    <p class=\"list-group-item-text\">The vCard message is actually a MMS message. Use the MMS API to check the message status.</p>\n" +
    "				  </a>				  \n" +
    "				</div>\n" +
    "\n" +
    "				</p>\n" +
    "			</div>\n" +
    "			\n" +
    "		\n" +
    "			<div class=\"alert alert-info row\">\n" +
    "				<p>\n" +
    "				<h4 class=\"alert-heading\">Usage</h4>\n" +
    "				</p> <br/>	\n" +
    "					\n" +
    "				<form class=\"form-horizontal\" ng-submit=\"sendvCardMessage()\">\n" +
    "				\n" +
    "				  <div class=\"form-group\">\n" +
    "					<label for=\"to\" class=\"col-sm-2 control-label\">To</label>\n" +
    "					<div class=\"col-sm-10\">\n" +
    "						<input type=\"text\" class=\"form-control\" ng-model=\"mmsMessage.to\" required=\"true\" id=\"to\" placeholder=\"To\"/>\n" +
    "					</div>\n" +
    "				  </div>\n" +
    "				  \n" +
    "				  <div class=\"form-group\">\n" +
    "					<label for=\"subject\" class=\"col-sm-2 control-label\">Subject</label>\n" +
    "					<div class=\"col-sm-10\">\n" +
    "	 			  		<input type=\"text\" class=\"form-control\" ng-model=\"mmsMessage.subject\" required=\"true\" id=\"subject\" placeholder=\"Subject\"/>\n" +
    "	 			  	</div>\n" +
    "				  </div>\n" +
    "\n" +
    "				  <div class=\"form-group\">\n" +
    "					<label for=\"text\" class=\"col-sm-2 control-label\">Body</label>	\n" +
    "					<div class=\"col-sm-10\">			\n" +
    "						<input type=\"text\" class=\"form-control\" ng-model=\"mmsMessage.body\" required=\"true\" id=\"text\" placeholder=\"Text\" />\n" +
    "					</div>\n" +
    "				  </div>\n" +
    "				\n" +
    "\n" +
    "				 <div class=\"form-group\">\n" +
    "					<label for=\"text\" class=\"col-sm-2 control-label\">vCard</label>	\n" +
    "					<div class=\"col-sm-10\">			\n" +
    "						<textarea id=\"vcard\" cols=\"40\" rows=\"5\" ng-model=\"mmsMessage.vcard\" required=\"true\">					    	\n" +
    "					    </textarea>\n" +
    "					</div>\n" +
    "				  </div>\n" +
    "			\n" +
    "				 <div class=\"form-group\">\n" +
    "				  	<div class=\"col-sm-offset-2 col-sm-10\">\n" +
    "				  		<br/>\n" +
    "					  <button type=\"submit\" class=\"btn btn-default\" ng-disabled=\"mmsMessageStatus\">Send vCard</button>\n" +
    "\n" +
    "					  <br/>\n" +
    "					  <br/>\n" +
    "					  {{mmsSendStatus}}\n" +
    "					</div>\n" +
    "				  </div>						\n" +
    "				</form>\n" +
    "			</div>	\n" +
    "\n" +
    "\n" +
    "		</div>\n" +
    "		\n" +
    "		</div>\n" +
    "	</div>\n" +
    "	\n" +
    "	\n" +
    "</div>\n" +
    "					\n" +
    "	\n" +
    "		\n" +
    "		\n" +
    "	\n" +
    "");
}]);

angular.module("video/video.tpl.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("video/video.tpl.html",
    "<div class=\"container-fluid\">\n" +
    "	<div class=\"row\">\n" +
    "		\n" +
    "		<div class=\"col-md-9\" id=\"content\">\n" +
    "			<div class=\"alert\">\n" +
    "				<p>\n" +
    "				<h3 class=\"alert-heading\">Video Gallery</h3>\n" +
    "				<br/>\n" +
    "				<div class=\"btn-group\">\n" +
    "				  <button type=\"button\" class=\"btn btn-warning\" ng-click=\"deleteVideos()\">Delete</button>\n" +
    "				</div>\n" +
    "				\n" +
    "				<br/><br/>\n" +
    "				<div class=\"radio row\">\n" +
    "					<label>\n" +
    "					 <input type=\"checkbox\" ng-model=\"selectedAll\" ng-click=\"checkAll()\">&nbsp;&nbsp;&nbsp;Select all\n" +
    "					</label>\n" +
    "				</div>\n" +
    "				</p> \n" +
    "			\n" +
    "				<div class=\"row-fluid row-margin-bottom\">                   \n" +
    "		              <div ng-repeat=\"video in videos\" >          	\n" +
    "		            	<div class='row' ng-show=\"video.dateTaken_CHANGED\"></div>\n" +
    "		            	<h3 ng-show=\"video.dateTaken_CHANGED\">{{video.dateTaken_DISPLAYED}}</h3>\n" +
    "			            <div  class=\"col-md-4 col-xs-6 col-sm-4 no-padding lib-item\" data-category=\"view\">\n" +
    "			                <div class=\"lib-panel\">\n" +
    "			                    <div class=\"row\">		                    \n" +
    "			                        <div class=\"col-md-5 col-xs-8 col-sm-4 lib-image-show\">\n" +
    "			                        	 <button class=\"btn btn-default\" ng-click=\"showVideo($index)\" >\n" +
    "							        		<img class=\"img-responsive\" uib-tooltip-placement=\"top\" uib-tooltip=\"{{video.displayName}}\" ng-src=\"{{streamVideoLink}}?uri={{video.contentUri}}&&id={{video.id}}&kind=1\" />\n" +
    "							        	 </button>                \n" +
    "			                        </div>\n" +
    "			                        \n" +
    "			                        <div class=\"lib-desc\">	                               \n" +
    "	                               		&nbsp;<input type=\"checkbox\" data-toggle=\"tooltip\" data-placement=\"right\" title=\"{{video.displayName}}\" ng-model=\"video.dateTaken_SELECTED\" />\n" +
    "	                            	</div>     \n" +
    "	                            		\n" +
    "			                    </div>\n" +
    "			                </div>\n" +
    "			            </div> 		            \n" +
    "	            	</div>\n" +
    "	        	</div>        				\n" +
    "				\n" +
    "			</div>\n" +
    "			\n" +
    "		</div>\n" +
    "	</div>	\n" +
    "</div>\n" +
    "	\n" +
    "");
}]);

angular.module("viewer/viewer.tpl.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("viewer/viewer.tpl.html",
    "<div class=\"container-fluid\">\n" +
    "        <div class=\"wrapper\" >\n" +
    "            <div ng-hide=\"show\">\n" +
    "                <div class=\"actionsDiv\">\n" +
    "                    Page size:\n" +
    "                    <select ng-model=\"entryLimit\">\n" +
    "                        <option>5</option>\n" +
    "                        <option>10</option>\n" +
    "                        <option>15</option>\n" +
    "                        <option>20</option>\n" +
    "                    </select>\n" +
    "                    Sorting by:\n" +
    "                    <select ng-model=\"predicate\" ng-change=\"sortBy(predicate)\">\n" +
    "                        <option>title &uarr;</option>\n" +
    "                        <option>title &darr;</option>\n" +
    "                        <option>date &uarr;</option>\n" +
    "                        <option>date &darr;</option>\n" +
    "                    </select>\n" +
    "                    Search:\n" +
    "                    <input type=\"search\" ng-model=\"searchText\" ng-change=\"search(searchText)\" placeholder=\"Enter string for search\"/>\n" +
    "                </div>\n" +
    "                <table>\n" +
    "                    <tr ng-repeat=\"imagesRow in imagesData | startFrom:(currentPage-1)*entryLimit/rowSize | limitTo:entryLimit/rowSize\">\n" +
    "                        <td ng-repeat=\"image in imagesRow\">\n" +
    "                            <img ng-src=\"{{ image.url }}\" err-src=\"{{defaultImg}}\" alt=\"{{image.title}}\" title=\"{{image.title}}\" ng-click=\"openImg(image)\"/><br/>\n" +
    "                            <input class=\"delete\" title=\"Delete\" type=\"button\" ng-click=\"deleteImg(image)\" value=\"x\"/>\n" +
    "                            <span>{{image.date}}</span><br/>\n" +
    "                        </td>\n" +
    "                    </tr>\n" +
    "                </table>\n" +
    "                <pagination total-items=\"totalItems\" items-per-page=\"entryLimit\" page=\"currentPage\" on-select-page=\"setPage(page)\" max-size=\"maxSize\" class=\"pagination-sm\" boundary-links=\"true\" rotate=\"false\"></pagination>\n" +
    "            </div>\n" +
    "            <div ng-show=\"show\" class=\"slideshow\">\n" +
    "                <input id=\"prev\" type=\"button\" ng-click=\"prevImg()\" value=\"<\"/>\n" +
    "                <div id=\"imgDiv\">\n" +
    "                    <img ng-src=\"{{currImg.url}}\" err-src=\"{{defaultImg}}\" alt=\"{{currImg.title}}\" />\n" +
    "                    <input class=\"delete\" title=\"Delete\" type=\"button\" ng-click=\"deleteImg(currImg)\" value=\"x\"/>\n" +
    "                </div>\n" +
    "                <input id=\"next\" type=\"button\" ng-click=\"nextImg()\" value=\">\"/>\n" +
    "                <input id=\"close\" title=\"Close\" type=\"button\" ng-click=\"closeImg()\" value=\"x\"/>\n" +
    "            </div>\n" +
    "        </div>\n" +
    "</div>");
}]);
