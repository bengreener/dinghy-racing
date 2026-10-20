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
   
package com.bginfosys.dinghyracing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class DinghyRacingApplication {

	public static void main(String[] args) {
		SpringApplication.run(DinghyRacingApplication.class, args);
	}
	
	@Bean
	String accessToken() {
		return "sl.u.AG3iLmxle0vDK95NrFz7mlc-HtC6D9Ji7B5gZvGSsIfW6bFZ7q0c1y7c3nBf0F6iLbmXUa_uRElq1oWHcc0BgoJ1yfY6RMLStrg-apn3JR-dbUaokp4mKXhPVsZwxH4w1QDDVLsOK6b5qqzQDnkqQ21azRXsyS3agJnLxaiBYO6EnQMEaBQM0Yxo_vvWy6tvCDnqHh55_6YlzWwfXRdsRlVSC8LpDlMnCNizjN7IPT-7-KL5j_djElRAjzodO8C_tm014VsjWQgqpJyjg7UhpdgM43YhNKSZPixrkh2UZ2kWhrJZY7SSg-pcNk7adP9dpAyUf0HXaJffxZ1ObvpW8JQhjzw8EfnE4R8W8JPii3xjYfn0-NEq7680dvekSrcWslMR2Nv11MqBMoQpB5DWaFeE4-eVqYeFr-Yk0nR2wQt6sNCtwSZyW2xQuEhJzNgyeDgY9fg4pbcydWcub2Zu4rqzGfRwPyi9YYmYSz_BcnynExDjxAKCG36GpfBU8RmSwsD5VOtNkUf7rbaxESITOTZuHLLv3gYAO2xwhVSnvZOAW6tT1HR6zIIk5hViV0Q48Tw4Te4aK9-wJndXCiSl2hNTIVv9uDhlSLVaNTDH3OpqPqaU-AVu6mvNyNy7-kR8MRB4GP9EnhWyA5C0-K7zZs5CYAd6tRVgzhJQK16tCJK223VfkjlA6x0IIPGVB8wiodkhL5A5ySpykzL4Vb1seEKNRqqOcfUN9lkgpthV7E7NWnFjTuGh-hBvGXXBjpNZalDLPwxTRPs38rw2Ty4DvgFvYY4oapkQN7xYgQNk1Frvb9EaVsczNqYymeKaM1aasKiPaYl_HrvcJ189kuLQgr_qX2pNqCTKVycxvX5b2-BdAM-6jlH_NPhcFFqdvOHBaZ11z9eQYM0Ih3edBvqrVF0z2JhPEOS84yVVRCKlXeyUgQkbeVyyC1C-_W4pzxSQeRpSi-Tm22pMw-qCy8opP2-aKvrrwaHgTLxOCBuqKbrN2a0wf0ehWitRZOWJhCNI2B1nHbUJJ2fxxD6XRSqf4Rt4Tgv7MxJyNCo5aIpy7ljGbTucTERKhX3l_A9zmfg82b085fNCZof1vOcgGH9s-4iVVtEEbcfbf2N4COBPqHF17IJw6yjOGhc7aa9PZpf2h2nGytFulLgFHFqzczPX1_RZDfAHYNypUkUH8v7P3HItOFhRECntdEu_zL7jXabU0GXZityRYcH88RpUmIb_1sWpuvWvVQ55kHrQ1oiAECpCd6npgsnu9rEEs7n8KliVfhKPCgcCrUJy-cTYUo-4IRmHxVOaT9dTUo6UKm-b_D7cRQ";
	}

}
