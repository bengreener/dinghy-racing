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

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

import com.bginfosys.dinghyracing.service.DropboxService;

@Controller
public class DropboxController {

	private final DropboxService dropboxService;
	
	public DropboxController(DropboxService dropboxService) {
		this.dropboxService = dropboxService;
	}
	
	@PostMapping("dinghyracing/api/dropbox/transferResultsFilesToDropbox")
	public ResponseEntity<Object> transferResultsFilesToDropbox() {
		dropboxService.transferResultsFilesToDropbox();
		return ResponseEntity.ok().build();
	}
}
