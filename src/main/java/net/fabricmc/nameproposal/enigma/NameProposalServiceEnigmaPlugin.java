/*
 * Copyright (c) 2021 FabricMC
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

package net.fabricmc.nameproposal.enigma;

import org.quiltmc.enigma.api.EnigmaPlugin;
import org.quiltmc.enigma.api.EnigmaPluginContext;
import org.quiltmc.enigma.api.service.JarIndexerService;
import org.quiltmc.enigma.api.service.NameProposalService;
import org.quiltmc.enigma.api.service.ObfuscationTestService;


public class NameProposalServiceEnigmaPlugin implements EnigmaPlugin {
	public static final String ID_PREFIX = "nameproposal:";

	@Override
	public void init(EnigmaPluginContext ctx) {
		ctx.registerService(ObfuscationTestService.TYPE, IntermediaryObfuscationTestService::new);

		EnigmaNameProposalService service = new EnigmaNameProposalService();
		ctx.registerService(JarIndexerService.TYPE, ctx1 -> service);
		ctx.registerService(NameProposalService.TYPE, ctx1 -> service);
	}
}
