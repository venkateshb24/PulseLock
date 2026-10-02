package pulselock_engine.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final StringRedisTemplate redisTemplate;
    private final RedisScript<Long> reserveStockScript;

    public void setStock(String itemId, int quantity) {
        redisTemplate.opsForValue().set("item:" + itemId + ":stock", String.valueOf(quantity));
    }

    public String attemptReservation(String itemId, int quantity) {
        String key = "item:" + itemId + ":stock";

        Long remainingStock = redisTemplate.execute(
                reserveStockScript,
                Collections.singletonList(key),
                String.valueOf(quantity)
        );

        if (remainingStock == null) {
            return "Error: Internal Redis error";
        }
        if(remainingStock == -1) {
            return "Error: Item not found";
        }
        if(remainingStock == -2) {
            return "SOLD_OUT: Not enough Inventory";
        }

        return "SUCCESS: Reserved. Remaining stock: " + remainingStock;
    }
}
