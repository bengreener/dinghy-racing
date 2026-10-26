/*
 * Copyright 2022-2024 BG Information Systems Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */

package com.bginfosys.dinghyracing.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import com.bginfosys.dinghyracing.model.DropboxToken;
import com.bginfosys.dinghyracing.persistence.DropboxTokenRepository;
import com.dropbox.core.DbxAppInfo;
import com.dropbox.core.DbxAuthFinish;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.DbxWebAuth;
import com.dropbox.core.TokenAccessType;
import com.dropbox.core.BadRequestException;
import com.dropbox.core.oauth.DbxCredential;
import com.dropbox.core.v2.DbxClientV2;

@Service
@ConditionalOnProperty(value= {"dropbox-appname", "dropbox-appkey", "dropbox-appsecret"})
public class DropboxService {
	
	private Logger logger = LoggerFactory.getLogger(DropboxService.class);
			
	private final String appKey;
	
	private final String appSecret;
	
	private final String appName;
	
	private final DropboxTokenRepository dropboxTokenRepository;
	
	public DropboxService(@Value("${dropbox-appkey}") String appKey, @Value("${dropbox-appsecret}") String appSecret,
			@Value("${dropbox-appname}") String appName, DropboxTokenRepository dropboxTokenRepository) {
		this.appKey = appKey;
		this.appSecret = appSecret;
		this.appName = appName;
		this.dropboxTokenRepository = dropboxTokenRepository;
	}
	
	public String getAuthUrl() {
		DbxRequestConfig config = DbxRequestConfig.newBuilder(appName).build();
		DbxAppInfo appInfo = new DbxAppInfo(appKey, appSecret);
		DbxWebAuth auth = new DbxWebAuth(config, appInfo);
		DbxWebAuth.Request authRequest = DbxWebAuth.newRequestBuilder()
				.withNoRedirect().withTokenAccessType(TokenAccessType.OFFLINE).build();
		return auth.authorize(authRequest);
	}
	
	public void getOAuthTokensFromCode(String code) {
		// Exceptions are handled by converting to RuntimeExceptions and using RestControllerAdvice to notify user
		// Expecting token issues that require user intervention to resolve i.e. generating a new OAuth code
		DbxRequestConfig config = DbxRequestConfig.newBuilder(appName).build();
		DbxAppInfo appInfo = new DbxAppInfo(appKey, appSecret);
		DbxWebAuth auth = new DbxWebAuth(config, appInfo);
		try {
			DbxAuthFinish authFinish = auth.finishFromCode(code);
			dropboxTokenRepository.save(new DropboxToken(authFinish.getAccessToken(), authFinish.getRefreshToken(), authFinish.getExpiresAt()));	
		}
		catch (BadRequestException exception) {
			logger.error("DropboxService.getOAuthTokensFromCode" + exception.getMessage());
			throw new DropboxException(exception.getMessage(), exception);
		}
		catch (DbxException exception) {
			logger.error("DropboxService.getOAuthTokensFromCode" + exception.getMessage());
			throw new DropboxException(exception.getMessage(), exception);
		}
	}
	
	public void transferResultsFilesToDropbox() {
		Optional<DropboxToken> optDropboxToken = dropboxTokenRepository.findFirstByOrderByIdDesc();
		if (optDropboxToken.isPresent()) {
			DropboxToken dropboxToken = optDropboxToken.get();
			DbxRequestConfig config = DbxRequestConfig.newBuilder(appName).build();
			DbxCredential credential = new DbxCredential(dropboxToken.getAccessToken(), dropboxToken.getExpiresAt(), dropboxToken.getRefreshToken(), appKey, appSecret);
			DbxClientV2 client = new DbxClientV2(config, credential);
			try (Stream<Path> paths = Files.list(Paths.get("results"))) {
				paths.forEach(p -> {
					try (InputStream inputStream = Files.newInputStream(p)) {
						transferFileToDropbox(client, inputStream, p.getFileName().toString());
					} catch (IOException exception) {
						logger.error("DropboxService.transferResultsFilesToDropbox" + exception.getMessage());
						throw new DropboxException(exception.getMessage(), exception);
					}	
				});
			} catch (IOException exception) {
				logger.error("DropboxService.transferResultsFilesToDropbox" + exception.getMessage());
				throw new DropboxException(exception.getMessage(), exception);
			}	
		}
		else {
			logger.debug("DropboxService.transferResultsFilesToDropbox Dropbox token not found for results transfer to Dropbox.");
			throw new DropboxException("Dropbox token not found for results transfer to Dropbox.");
		}
	}
	
	private void transferFileToDropbox(DbxClientV2 client, InputStream inputStream, String filename) {
		try {
            client.files().uploadBuilder("/" + filename).uploadAndFinish(inputStream);
        }
		catch (Exception exception) {
			logger.error("DropboxService.transferFilesToDropbox" + exception.getMessage());
			throw new DropboxException(exception.getMessage(), exception);
		}
	}
}
