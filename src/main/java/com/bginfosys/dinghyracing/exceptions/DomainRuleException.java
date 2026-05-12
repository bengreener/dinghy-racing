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

package com.bginfosys.dinghyracing.exceptions;

/**
 * Abstract cLass to base errors arising from conflict with domain rules.
 * Purpose is to enable feed back to user that a domain rule has resulted in the requested operation failing.
 */
public abstract class DomainRuleException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;
	
	public DomainRuleException(String message) {
		super(message);
	}
	
	public DomainRuleException(String message, Throwable cause) {
		super(message, cause);
	}
	
	// Prevent stack trace from being exposed to client
	@Override
	public synchronized Throwable fillInStackTrace() {
		return this;
	}

}
