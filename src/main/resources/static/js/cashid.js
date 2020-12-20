(function(cashid) {
	window.addEventListener("load", function() {
		let csrfElement = document.getElementById("csrf");
		cashid.msg = "";
		var address;

		var loginElements = document.querySelectorAll('.cashid-login');
		
		for (var i = 0; i < loginElements.length; i++) {
			loginElements[i].addEventListener('click', function() {
				if (typeof web4bch === "undefined" || typeof web4bch.bch === "undefined") {
					jsUtilsAlert("browser-extension-not-found", "browser-extension-needed");
					return;
				} else {
					address = web4bch.bch.defaultAccount;
					if (typeof address === "undefined") {
						jsUtilsAlert("browser-extension-not-active", "activate-browser-extension");
						return;
					}
				}
				let authData = cashid.initForm();
				authData.append("address", address);
				let xhr = new XMLHttpRequest();
				xhr.open("POST", "/requestnonce");
				xhr.onload = function() {
					if (xhr.readyState === 4 && xhr.status === 200) {
						let nonce = xhr.responseText;
						let url = "cashid:" + window.location.host + "/cashid/?x=" + nonce;
						web4bch.bch.sign(web4bch.bch.defaultAccount, url, function(err, res) {
							if (res) {
								authData.append("nonce", nonce);
								let xhrb = new XMLHttpRequest();
								xhrb.open("POST", "/cashidauth");
								xhrb.onload = function(e) {
									if (xhrb.readyState === 4 && xhrb.status === 200) {
										window.location.reload();
									}
								};
								xhrb.send(authData);
							} else {
								alert("Error. Maybe you're trying that too often");
								return;
							}
						});
					} else {
						alert("Error whilst requesting nonce: " + xhr.statusText + ". " + xhr.responseText + ".");
					}
				};
				xhr.onerror = function(e) {
					let errorMsg = "Error whilst requesting nonce: " + e.error;
					console.log(errorMsg);
					alert(errorMsg);
				}
				xhr.send(authData);
			}, false);
		}

		if (document.getElementById("cashid-logout")) {
			document.getElementById("cashid-logout").addEventListener("click", function() {
				let xhr = new XMLHttpRequest();
				xhr.open("POST", "/logout");
				xhr.onload = function() {
					window.location.reload(true);
				};
				xhr.send(cashid.initForm());
			}, false);
		}

		cashid.initForm = function() {
			let authData = new FormData();
			authData.append(csrfElement.getAttribute("name"), csrfElement.getAttribute("value"));
			return authData;
		};
	});
}(window.cashid = window.cashid || {}));
