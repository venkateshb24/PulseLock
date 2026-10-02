-- KEYS[1]: The item stock key (e.g., "item:101:stock")
-- ARGV[1]: Quantity requested (e.g., "1")

local current_stock = redis.call('GET', KEYS[1])

if not current_stock then
    return -1 -- Item does not exist
end

current_stock = tonumber(current_stock)
local requested_qty = tonumber(ARGV[1])

if current_stock >= requested_qty then
    -- Automatically deduct stock
    redis.call('DECRBY', KEYS[1], requested_qty)
    return current_stock - requested_qty -- Return remaining stock
else
    return -2 --Insufficient stock
end