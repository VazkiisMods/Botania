package vazkii.botania.common.impl.corporea;

import vazkii.botania.api.corporea.CorporeaHelper;

import static vazkii.botania.api.BotaniaAPI.botaniaRL;

public final class DefaultCorporeaMatchers {
	public static void init() {
		CorporeaHelper.instance().registerRequestMatcher(botaniaRL("string"), CorporeaStringMatcher.class, CorporeaStringMatcher::createFromNBT);
		CorporeaHelper.instance().registerRequestMatcher(botaniaRL("item_stack"), CorporeaItemStackMatcher.class, CorporeaItemStackMatcher::createFromNBT);
	}

	private DefaultCorporeaMatchers() {}
}
