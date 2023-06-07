package me.shurik.scorewatch.subscription;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.jetbrains.annotations.Nullable;

public class ScoreSubscriptions {
    /**
     * Map of player UUIDs to their subscriptions.
     */
    private static final Map<UUID, List<ScoreSubscription>> playerSubscriptions = new HashMap<UUID, List<ScoreSubscription>>();
    /**
     * List of global subscriptions.
     */
    private static final List<ScoreSubscription> globalSubscriptions = new ArrayList<ScoreSubscription>();

    private static boolean globalSubscriptionsEnabled = true;

    private ScoreSubscriptions() {}

    /**
     * Reset all subscriptions.
     */
    public static void reset() {
        playerSubscriptions.clear();
        globalSubscriptions.clear();
    }

    /**
     * Reset global subscriptions.
     */
    public static void resetGlobal() {
        globalSubscriptions.clear();
    }

    /**
     * Switch global subscriptions on/off.
     * @return The new state of global subscriptions.
     */
    public static boolean switchGlobal() {
        globalSubscriptionsEnabled = !globalSubscriptionsEnabled;
        return globalSubscriptionsEnabled;
    }

    /**
     * Check if global subscriptions are enabled.
     * @return State of global subscriptions.
     */
    public static boolean globalEnabled() {
        return globalSubscriptionsEnabled;
    }

    /**
     * Get all subscriptions for the given player.
     * @param player The player to get subscriptions for.
     * @return The list of subscriptions for the given player.
     */
    public static List<ScoreSubscription> getSubscriptions(UUID player) {
        if (playerSubscriptions.containsKey(player)) {
            return playerSubscriptions.get(player);
        } else {
            List<ScoreSubscription> subscriptions = new ArrayList<ScoreSubscription>();
            playerSubscriptions.put(player, subscriptions);
            return subscriptions;
        }
    }

    /**
     * Get all global subscriptions.
     * @return The list of global subscriptions.
     */
    public static List<ScoreSubscription> getGlobalSubscriptions() {
        return globalSubscriptions;
    }

    /**
     * Add player subscription for the given objective and score holder.
     * @param player The player to subscribe.
     * @param objective The objective to subscribe to.
     * @param scoreHolder The score holder to subscribe to.
     * @return True if subscription was added, false if it was already present.
     */
    public static boolean isSubscribed(UUID player, String objective, @Nullable String scoreHolder) {
        return isSubscribed(getSubscriptions(player), objective, scoreHolder);
    }

    /**
     * Add global subscription to the list for the given objective and score holder.
     * @param objective The objective to subscribe to.
     * @param scoreHolder The score holder to subscribe to.
     * @return True if subscription was added, false if it was already present.
     */
    public static boolean isGloballySubscribed(String objective, @Nullable String scoreHolder) {
        return isSubscribed(globalSubscriptions, objective, scoreHolder);
    }

    /**
     * Checks for subscription in the list for the given objective or objective + score holder.
    * @param subscriptions The list of subscriptions to check.
    * @param objective The objective to check.
    * @param scoreHolder The score holder to check.
    * @return True if subscription was found, false otherwise.
    */
    public static boolean isSubscribed(List<ScoreSubscription> subscriptions, String objective, @Nullable String scoreHolder) {
        for (ScoreSubscription subscription : subscriptions) {
            if (subscription.match(objective, scoreHolder)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Remove all subscriptions of the given player.
     * @param player The player to unsubscribe.
     * @return True if subscriptions were removed, false if player had no subscriptions.
     */
    public static boolean unsubscribeAll(UUID player) {
        boolean result = getSubscriptions(player).size() == 0;
        playerSubscriptions.remove(player);
        return !result;
    }

    /**
     * Add subscription to the list for the given objective and score holder.
     * @param player The player to subscribe.
     * @param objective The objective to subscribe to.
     * @param scoreHolder The score holder to subscribe to.
     * @return True if subscription was added, false if it was already present.
     */
    public static boolean unsubscribe(UUID player, String objective, @Nullable String scoreHolder) {
        return unsubscribe(getSubscriptions(player), objective, scoreHolder);
    }
    
    /**
     * Remove global subscription for the given objective and score holder.
     * @param objective The objective to unsubscribe from.
     * @param scoreHolder The score holder to unsubscribe from.
     * @return True if subscription was found and removed, false otherwise.
     */
    public static boolean unsubscribeGlobal(String objective, @Nullable String scoreHolder) {
        return unsubscribe(globalSubscriptions, objective, scoreHolder);
    }

    /**
     * Remove subscription from the list for the given objective and score holder.
     * @param subscriptions The list of subscriptions to remove from.
     * @param objective The objective to unsubscribe from.
     * @param scoreHolder The score holder to unsubscribe from.
     * @return True if subscription was found and removed, false otherwise.
     */
    public static boolean unsubscribe(List<ScoreSubscription> subscriptions, String objective, @Nullable String scoreHolder) {
        // Better use index-based loop to avoid unnecessary iterations when removing
        for (int i = 0; i < subscriptions.size(); i++) {
            ScoreSubscription subscription = subscriptions.get(i);
            if (subscription.exactMatch(objective, scoreHolder)) {
                subscriptions.remove(i);
                return true;
            }
        }
        return false;
    }
    
    /**
     * Get players, subscribed to the given objective and score holder.
     * @param objective The objective to check.
     * @param scoreHolder The score holder to check.
     * @return List of players subscribed to the given objective and score holder.
     */
    public static List<UUID> getSubscribers(String objective, @Nullable String scoreHolder) {
        List<UUID> subscribers = new ArrayList<UUID>();
        for (Map.Entry<UUID, List<ScoreSubscription>> entry : playerSubscriptions.entrySet()) {
            for (ScoreSubscription subscription : entry.getValue()) {
                if (subscription.match(objective, scoreHolder)) {
                    subscribers.add(entry.getKey());
                }
            }
        }

        return subscribers;
    }

    /**
     * Get players, subscribed to the given objective.
     * @param player The player to check.
     * @param objective The objective to check.
     * @param scoreHolder The score holder to check.
     * @return List of players subscribed to the given objective and score holder.
     */
    public static boolean subscribe(UUID player, String objective, @Nullable String scoreHolder) {
        return subscribe(getSubscriptions(player), objective, scoreHolder);
    }

    /**
     * Add global subscription for the given objective and score holder.
     * @param objective The objective to subscribe to.
     * @param scoreHolder The score holder to subscribe to.
     * @return True if subscription was added, false if it was already present.
     */
    public static boolean subscribeGlobal(String objective, @Nullable String scoreHolder) {
        return subscribe(globalSubscriptions, objective, scoreHolder);
    }
    /**
     * Add subscription to the list for the given objective and score holder.
     * @param subscriptions The list of subscriptions to add to.
     * @param objective The objective to subscribe to.
     * @param scoreHolder The score holder to subscribe to.
     * @return True if subscription was added, false if it was already present.
     */
    public static boolean subscribe(List<ScoreSubscription> subscriptions, String objective, @Nullable String scoreHolder) {
        // Check if subscription is valid
        if (objective == null) {
            throw new IllegalArgumentException("Objective cannot be null");
        }
        // Check if this or more general subscription already exists
        if (!isSubscribed(subscriptions, objective, scoreHolder)) {
            subscriptions.add(new ScoreSubscription(objective, scoreHolder));
            return true;
        }
        return false;
    }
}