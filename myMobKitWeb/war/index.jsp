<!DOCTYPE html>
<html lang="en">
<head>

<title>myMobKit</title>

<%@ include file="header.jsp"%>

<body data-spy="scroll">

	<%@ include file="common.jsp"%>

	<div class="container">
		<div class="row">
			<div class="col-md-4 col-lg-offset-1">
				<div class="android">
					<div id="carousel-screen" class="carousel slide"
						data-ride="carousel">
						<ol class="carousel-indicators">
							<li data-target="#carousel-screen" data-slide-to="0"
								class="active"></li>
							<li data-target="#carousel-screen" data-slide-to="1" class=""></li>
							<li data-target="#carousel-screen" data-slide-to="2" class=""></li>
							<li data-target="#carousel-screen" data-slide-to="3" class=""></li>
						</ol>
						<div class="carousel-inner">
							<div class="active item">
								<img src="img/screen.png" class="img-responsive" />
							</div>
							<div class="item">
								<img src="img/screen1.png" class="img-responsive" />
							</div>
							<div class="item">
								<img src="img/screen2.png" class="img-responsive" />
							</div>
							<div class="item">
								<img src="img/screen3.png" class="img-responsive" />
							</div>
						</div>
					</div>
				</div>
			</div>
			<div class="col-md-6">
				<br/>
				<img src="img/logo.png" alt="myMobKit" class="img-responsive" />
				<div id="features">
					<div id="myTabContent" class="tab-content">
						<div class="tab-pane fade active in" id="about">
							<p>
								Download the app today for free!
							</p>

							<p>
							<ul id="features">
								<li>Built-in web server to access your phone.</li>
								<li>Use your phone camera for remote surveillance.</li>
								<li>Remotely view and control your phone through the web
									control panel.</li>
								<li>Remotely start up surveillance feature using SMS.</li>
								<li>Control access to the app through lock pattern.</li>
								<li><a
									href="http://www.codeproject.com/Articles/791145/Motion-Detection-in-Android-Howto">Motion
										detection</a> and face detection using different algorithms.</li>
								<li>Trigger alarm through SMS or email at configured
									interval and triggers.</li>
								<li>Save detected images to cloud storage or attach in email.</li>
								<li>Manual or motion activated video recording.</li>
								<li>Integrated with Google Drive.</li>
								<li>Ability to disguise the camera and close the app when device is touched.</li>
								<li>Turn your phone into a <a
									href="http://www.codeproject.com/Articles/791991/Use-Android-Phone-as-Webcam-and-Surveillance-Camer">webcam
										or remote surveillance camera</a> using any applications that
									support DirectShow API, e.g. Skype or VLC Media Player.
								</li>
								<li>Turn your phone into a <a href="http://www.codeproject.com/Articles/857539/SMS-Gateway-using-Android-Phone">SMS gateway</a> - support REST APIs
									for SMS sending and receiving.</li>
								<li><a href="http://www.codeproject.com/Articles/883646/AngularJS-App-to-access-Android-Phone-Gallery">REST APIs</a> to manipulate contact, photo, video and audio.</li>
								<li>REST APIs to send MMS.</li>
							</ul>
							</p>
							
						</div>
					</div>
				</div>

				<a href="https://play.google.com/store/apps/details?id=com.mymobkit&hl=en" class="appstore"><img src="img/google_play.png"
					alt="Available at Google play" class="img-responsive" /></a>
			</div>
		</div>
		<hr />

		<div class="row">

			<%@ include file="footer.jsp"%>

		</div>

	</div>
	
</body>

</html>
