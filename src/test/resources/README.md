The original file was fetched from https://github.com/annoy4s/annoy4s/blob/0.9.0/src/test/resources/searchk-test-vector and then reshaped into JSON using `jq`

```
wget https://raw.githubusercontent.com/annoy4s/annoy4s/0.9.0/src/test/resources/searchk-test-vector
cat searchk-test-vector | jq --raw-input --slurp 'split("\n") | map(split(" ")) | map( { "id": .[0], "vectors": [(.[1:][] | tonumber)], "merchant": "test" } )' > test-vector.json
```

Note: You may need to delete the trailing newline.
