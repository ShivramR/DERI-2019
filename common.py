import csv
import pandas as pd

f = open("config.txt", "r")

file_rows = []

for row in f:
    file_rows.append(row)

num_years = int(file_rows[0])
start_year = int(file_rows[1])

years = {}
year_files = {}
write_files = {}

for d in range(0, num_years):
    year_files[d] = str(start_year+d) + "/Iterative-score-users.csv"
    write_files[d] = str(start_year+d) + "/Iterative-score-users-" + str(start_year+d) + ".csv"

in_common = False

storage_list = []
storage_2 = []

for e in range(0, num_years):
    with open(year_files[e], "r") as a:
        storage_2.append(list(csv.reader(a)))
        a_year = pd.DataFrame(storage_2[e])
        a_year.drop(a_year.columns[1], axis=1, inplace=True)
        storage_list.append(a_year.values.tolist())

if num_years == 3:
    common = [x for x in storage_list[0] if x in storage_list[1] and x in storage_list[2]]
elif num_years == 4:
    common = [x for x in storage_list[0] if x in storage_list[1] and x in storage_list[2] and x in storage_list[3]]
else:
    common = [x for x in storage_list[0] if x in storage_list[1] and x in storage_list[2]]

df = pd.DataFrame(common)
df.drop_duplicates(inplace=True)

print("Length of common = " + str(len(df.index)))

if not in_common:
    df.to_csv('Common.csv')

for w in range(0, num_years):
    with open(write_files[w], 'w') as file:
        writer = csv.writer(file, delimiter=',')
        for i in storage_2[w]:
            for j in common:
                if i[0] in j:
                    writer.writerow(i)
