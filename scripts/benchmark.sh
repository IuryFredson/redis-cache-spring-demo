BASE_URL=${BASE_URL:-http://localhost:8080}
PRODUCT_ID=${PRODUCT_ID:-1}
RUNS=${RUNS:-10}

printf "endpoint,run,source,durationMs,curlTotalSeconds\n"

curl -s -X DELETE "$BASE_URL/cache/products/$PRODUCT_ID" > /dev/null

for run in $(seq 1 "$RUNS"); do
  response=$(curl -s -w "\n%{time_total}" "$BASE_URL/products/$PRODUCT_ID")
  body=$(printf "%s" "$response" | sed '$d')
  total=$(printf "%s" "$response" | tail -n 1)
  source=$(printf "%s" "$body" | sed -n 's/.*"source":"\([A-Z]*\)".*/\1/p')
  duration=$(printf "%s" "$body" | sed -n 's/.*"durationMs":\([0-9.]*\).*/\1/p')
  printf "cached,%s,%s,%s,%s\n" "$run" "$source" "$duration" "$total"
done

for run in $(seq 1 "$RUNS"); do
  response=$(curl -s -w "\n%{time_total}" "$BASE_URL/products/$PRODUCT_ID/no-cache")
  body=$(printf "%s" "$response" | sed '$d')
  total=$(printf "%s" "$response" | tail -n 1)
  source=$(printf "%s" "$body" | sed -n 's/.*"source":"\([A-Z]*\)".*/\1/p')
  duration=$(printf "%s" "$body" | sed -n 's/.*"durationMs":\([0-9.]*\).*/\1/p')
  printf "no-cache,%s,%s,%s,%s\n" "$run" "$source" "$duration" "$total"
done
