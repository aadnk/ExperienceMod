package com.comphenix.xp.lookup;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.NotImplementedException;
import org.bukkit.potion.PotionType;
import com.comphenix.xp.Range;

public class PotionTree extends SearchTree<PotionQuery, Range> {

	private Parameter<PotionType> type = new Parameter<PotionType>();
	private Parameter<Integer> level = new Parameter<Integer>();
	private Parameter<Boolean> extended = new Parameter<Boolean>();
	private Parameter<Boolean> splash = new Parameter<Boolean>();

	@Override
	protected Integer putFromParameters(PotionQuery source, Integer id) {

		int paramCount = 0;
		
		// Add parameters
		if (source.hasType()) {
			type.put(source.getType(), id); paramCount++;
		}
		
		if (source.hasLevel()) {
			level.put(source.getLevel(), id); paramCount++;
		}
		
		if (source.hasExtended()) {
			extended.put(source.getExtended(), id); paramCount++;
		}
		
		if (source.hasSplash()) {
			splash.put(source.getSplash(), id); paramCount++;
		}
		
		return paramCount;
	}

	@Override
	protected Set<Integer> getFromParameters(PotionQuery source) {

		Set<Integer> candidates = new HashSet<Integer>(flatten.keySet());
		
		// Filter by parameters
		if (source.hasType()) {
			type.retain(candidates, source.getType());
		}
		
		if (source.hasLevel()) {
			level.retain(candidates, source.getLevel());
		}
		
		if (source.hasExtended()) {
			extended.retain(candidates, source.getExtended());
		}
			
		if (source.hasSplash()) {
			splash.retain(candidates, source.getSplash());
		}
			
		return candidates;
	}
	
	// I always end up making these ugly hacks. Damn it.
	public ItemTree getItemQueryAdaptor() {
		return new ItemTree() {
			@Override
			public Range get(ItemQuery element) {
				return PotionTree.this.get(new PotionQuery(element));
			}

			@Override
			public boolean containsKey(ItemQuery element) {
				return PotionTree.this.containsKey(new PotionQuery(element));
			}
			
			@Override
			public Integer put(ItemQuery element, Range value) {
				throw new NotImplementedException();
			}
			
			@Override
			protected Integer putFromParameters(ItemQuery source, Integer id) {
				throw new NotImplementedException();
			}

			@Override
			protected Set<Integer> getFromParameters(ItemQuery source) {
				throw new NotImplementedException();
			}
		};
	}
}
