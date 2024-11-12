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
   
package com.bginfosys.dinghyracing.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.bginfosys.dinghyracing.model.Dinghy;
import com.bginfosys.dinghyracing.model.DinghyClass;

public interface DinghyRepository extends JpaRepository<Dinghy, Long> {
	
	@SuppressWarnings("unchecked")
	@Override
	Dinghy save(@Param("dinghy") Dinghy dinghy);
	
	@Override
	void deleteById(@Param("id") Long id);
	
	@Override
	void delete(@Param("dinghy") Dinghy dinghy);	
	
	Page<Dinghy> findByDinghyClass(@Param("dinghyClass") DinghyClass dinghyClass, Pageable pageable);

	Dinghy findBySailNumberAndDinghyClass(@Param("sailNumber") String sailNumber, @Param("dinghyClass") DinghyClass dinghyClass);
	
	Page<Dinghy> findBySailNumber(@Param("sailNumber") String sailNumber, Pageable pageable);
}
