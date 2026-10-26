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

package com.bginfosys.dinghyracing.web.controllers;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.data.rest.webmvc.BasePathAwareController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.bginfosys.dinghyracing.service.DropboxService;
import com.bginfosys.dinghyracing.service.ExportService;
import com.bginfosys.dinghyracing.web.dto.DropboxUrlDTO;

@BasePathAwareController
@ConditionalOnBean(com.bginfosys.dinghyracing.service.DropboxService.class)
public class DropboxController {
	
	private final DropboxService dropboxService;
	
	private final ExportService exportService;
	
	public DropboxController(DropboxService dropboxService, ExportService exportService) {
		this.dropboxService = dropboxService;
		this.exportService = exportService;
	}
	
	@GetMapping("/dropbox/getAuthUrl")
	public ResponseEntity<DropboxUrlDTO> getDroboxAuthUrl() {
		DropboxUrlDTO dropboxUrlDTO = new DropboxUrlDTO(dropboxService.getAuthUrl());
		return ResponseEntity.ok(dropboxUrlDTO);
	}
	
	@PostMapping("/dropbox/getAuthTokens")
	public ResponseEntity<Object> getDropboxAuthTokens(@RequestParam(value="code") String code) {
		dropboxService.getOAuthTokensFromCode(code);
		return ResponseEntity.ok().build();
	}
	
	@PostMapping("/dropbox/transferResultsFilesToDropbox")
	public ResponseEntity<Object> transferResultsFilesToDropbox() {
		dropboxService.transferResultsFilesToDropbox();
		exportService.deleteAllResultsFiles();
		return ResponseEntity.ok().build();
	}
	
}
