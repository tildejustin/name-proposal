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

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.objectweb.asm.tree.ClassNode;
import org.quiltmc.enigma.api.analysis.index.jar.EntryIndex;
import org.quiltmc.enigma.api.analysis.index.jar.JarIndex;
import org.quiltmc.enigma.api.class_provider.ClassProvider;
import org.quiltmc.enigma.api.service.JarIndexerService;
import org.quiltmc.enigma.api.service.NameProposalService;
import org.quiltmc.enigma.api.source.TokenType;
import org.quiltmc.enigma.api.translation.mapping.EntryMapping;
import org.quiltmc.enigma.api.translation.mapping.EntryRemapper;
import org.quiltmc.enigma.api.translation.representation.entry.Entry;
import org.quiltmc.enigma.api.translation.representation.entry.FieldEntry;
import org.quiltmc.enigma.api.translation.representation.entry.MethodEntry;

import net.fabricmc.nameproposal.MappingEntry;
import net.fabricmc.nameproposal.NameFinder;

public class EnigmaNameProposalService implements JarIndexerService, NameProposalService {
	private Map<String, String> recordNames;
	Map<MappingEntry, String> fieldNames;

	@Override
	public void acceptJar(Set<String> classNames, ClassProvider classProvider, JarIndex jarIndex) {
		NameFinder nameFinder = new NameFinder();

		for (String className : classNames) {
			ClassNode classNode = classProvider.get(className);
			nameFinder.accept(Objects.requireNonNull(classNode, "Failed to get ClassNode for " + className));
		}

		recordNames = nameFinder.getRecordNames();
		fieldNames = nameFinder.getFieldNames();
	}

	@Override
	public Map<Entry<?>, EntryMapping> getProposedNames(JarIndex jarIndex) {
		Map<Entry<?>, EntryMapping> mappings = new HashMap<>();
		Objects.requireNonNull(recordNames, "Cannot proposeName before indexing");
		for (FieldEntry fieldEntry : jarIndex.getIndex(EntryIndex.class).getFields()) {
			if (fieldEntry.getName().startsWith("comp_")) {
				Optional.ofNullable(recordNames.get(fieldEntry.getName())).ifPresent(mapping -> mappings.put(fieldEntry, this.createMapping(mapping, TokenType.JAR_PROPOSED)));
			}
			Optional.ofNullable(
					fieldNames.get(new MappingEntry(fieldEntry.getContainingClass().getFullName(), fieldEntry.getName(), fieldEntry.getDesc().toString()))
			).ifPresent(mapping ->
					mappings.put(fieldEntry, new EntryMapping(mapping))
			);
		}
		for (MethodEntry methodEntry : jarIndex.getIndex(EntryIndex.class).getMethods()) {
			if (methodEntry.getName().startsWith("comp_")) {
				Optional.ofNullable(recordNames.get(methodEntry.getName())).ifPresent(mapping -> mappings.put(methodEntry, this.createMapping(mapping, TokenType.JAR_PROPOSED)));
			}
		}
		return mappings;
	}

	@Override
	public Map<Entry<?>, EntryMapping> getDynamicProposedNames(EntryRemapper entryRemapper, Entry<?> obfEntry, EntryMapping entryMapping, EntryMapping entryMapping1) {
		return null;
	}

	@Override
	public String getId() {
		return NameProposalServiceEnigmaPlugin.ID_PREFIX + "name_proposal";
	}
}
