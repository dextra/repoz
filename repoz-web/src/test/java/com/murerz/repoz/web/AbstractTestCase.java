package com.murerz.repoz.web;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.After;
import org.junit.Before;

import com.googlecode.mycontainer.commons.http.HttpClientRequestService;
import com.googlecode.mycontainer.commons.http.Request;
import com.googlecode.mycontainer.commons.http.RequestAdapter;
import com.googlecode.mycontainer.commons.http.Response;
import com.murerz.repoz.web.util.CryptUtil;

public class AbstractTestCase {

	protected MycontainerHelper mycontainer;
	protected int port;
	protected HttpClientRequestService s;
	protected RequestAdapter a;

	@Before
	public void setUp() {
		mycontainer = MycontainerHelper.me();
		mycontainer.setUp();
		port = mycontainer.bind(0);

		s = new HttpClientRequestService("http://localhost:" + port);
		a = new RequestAdapter(s);
	}

	@After
	public void tearDown() {
		if (mycontainer != null) {
			mycontainer.unbindPort(port);
			mycontainer.tearDown();
		}
	}

	public void assertResp(Request req, int code, String mediaType, String charset, String data) {
		Response resp = s.execute(req);
		assertEquals(new Integer(code), resp.code());
		assertEquals(mediaType, resp.content().mediaType());
		if (charset != null) {
			assertEquals(charset, resp.content().charset());
			assertEquals(data, resp.content().text());
		} else {
			assertNull(charset, resp.content().charset());
			assertEquals(data, new String(resp.content().data()));
		}

	}

	public Response execute(String basic, String method, String uri, String contentType, String text) {
		Request req = Request.create(method, uri);
		if (contentType != null) {
			req.contentType(contentType).content(text);
		}
		if (basic != null) {
			basic = CryptUtil.encodeBase64String(basic, "UTF-8");
			req.headers().add("Authorization", "Basic " + basic);
		}
		Response ret = s.execute(req);
		return ret;
	}

	public void setAccess(String querystring) {
		assertEquals(new Integer(200), execute("main:123", "POST", "/access", "application/x-www-form-urlencoded;charset=UTF-8", querystring).code());
	}

}
