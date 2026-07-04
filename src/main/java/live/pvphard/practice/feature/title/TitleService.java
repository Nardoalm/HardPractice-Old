package live.pvphard.practice.feature.title;

import live.pvphard.practice.feature.kit.Kit;
import live.pvphard.practice.bootstrap.lifecycle.Service;
import live.pvphard.practice.feature.title.model.TitleRecord;

import java.util.Map;

/**
 * @author Remi
 * @project hard-practice
 * @date 2/07/2025
 */
public interface TitleService extends Service {
    /**
     * Gets the map of all loaded titles.
     * @return A map where the key is the Kit and the value is the TitleRecord.
     */
    Map<Kit, TitleRecord> getTitles();
}
