/*
 * Copyright 2019 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terasology.logic.market;

import org.terasology.assets.management.AssetManager;
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.prefab.Prefab;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.input.ButtonState;
import org.terasology.input.binds.market.MarketButton;
import org.terasology.logic.inventory.ItemComponent;
import org.terasology.network.ClientComponent;
import org.terasology.registry.In;
import org.terasology.registry.Share;
import org.terasology.rendering.nui.NUIManager;
import org.terasology.world.block.Block;
import org.terasology.world.block.BlockExplorer;
import org.terasology.world.block.BlockManager;
import org.terasology.world.block.BlockUri;
import org.terasology.world.block.family.BlockFamily;
import org.terasology.world.block.items.BlockItemFactory;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@RegisterSystem(RegisterMode.CLIENT)
@Share(MarketClientSystem.class)
public class MarketClientSystem extends BaseComponentSystem {
    private Set<Prefab> purchasablePrefabs = new HashSet<>();
    private Set<Block> purchasableBlocks = new HashSet<>();
    private BlockItemFactory blockItemFactory;

    @In
    private NUIManager nuiManager;
    @In
    private EntityManager entityManager;
    @In
    private AssetManager assetManager;
    @In
    private BlockManager blockManager;


    @ReceiveEvent(components = ClientComponent.class)
    public void onToggleMarket(MarketButton event, EntityRef entity) {
        if (event.getState() == ButtonState.DOWN) {
            nuiManager.toggleScreen("Market:marketScreen");
            event.consume();
        }
    }

    @Override
    public void postBegin() {
        blockItemFactory = new BlockItemFactory(entityManager);
        BlockExplorer blockExplorer = new BlockExplorer(assetManager);

        purchasablePrefabs = assetManager.getLoadedAssets(Prefab.class)
                .stream()
                .filter(prefab -> prefab.hasComponent(ItemComponent.class)
                        && prefab.hasComponent(PurchasableComponent.class))
                .collect(Collectors.toSet());

        Set<BlockUri> blocks = new HashSet<>();
        blocks.addAll(blockManager.listRegisteredBlockUris());
        blocks.addAll(blockExplorer.getAvailableBlockFamilies());
        blocks.addAll(blockExplorer.getFreeformBlockFamilies());

        purchasableBlocks = blocks.stream()
                .map(blockManager::getBlockFamily)
                .map(BlockFamily::getArchetypeBlock)
                .filter(block -> block.getPrefab().isPresent())
                .filter(block -> block.getPrefab().get().hasComponent(PurchasableComponent.class))
                .collect(Collectors.toSet());
    }

    public Set<Prefab> getPurchasablePrefabs() {
        return purchasablePrefabs;
    }

    public Set<Block> getPurchasableBlocks() {
        return purchasableBlocks;
    }
}
