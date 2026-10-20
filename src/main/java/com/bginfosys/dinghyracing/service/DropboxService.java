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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.UploadErrorException;

@Service
public class DropboxService {
	
	private final DbxRequestConfig config;
	
	private final DbxClientV2 client;
	
	public DropboxService(String accessToken) {
		super();
		// Create Dropbox client
        this.config = DbxRequestConfig.newBuilder("DinghyRacingResultsUploader").build();
        this.client = new DbxClientV2(config, accessToken);
	}
	
	public void transferResultsFilesToDropbox() {
		try (Stream<Path> paths = Files.list(Paths.get("results"))) {
			paths.forEach(p -> {
				try (InputStream inputStream = Files.newInputStream(p)) {
					transferFileToDropbox(inputStream, p.getFileName().toString());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
			});
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void transferFileToDropbox(InputStream inputStream, String filename) throws IOException {
		try {
            client.files().uploadBuilder("/" + filename).uploadAndFinish(inputStream);
        }
		catch (UploadErrorException ex) {
			throw new IOException(ex);
		}
		catch (DbxException ex) {
			throw new IOException(ex);
		}
	}
}
