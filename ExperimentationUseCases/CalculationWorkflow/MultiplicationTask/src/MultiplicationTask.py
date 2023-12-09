import json
import sys
import os

path_to_task_results_file = os.environ.get("PATH_TO_TASK_RESULTS_FILE")
path_to_work_directory = os.environ.get("PATH_TO_WORK_DIRECTORY")


def multiply_arguments(arguments_json):
    arguments = arguments_json["arguments"]
    multiplication_result = 1
    for arg in arguments:
        multiplication_result *= int(arg)
    return multiplication_result


def create_and_dump_json_to_file(result_data, filename):
    with open(filename, "w") as file:
        json.dump(result_data, file)


def write_to_file(data, filename, mode):
    os.makedirs(os.path.dirname(filename), exist_ok=True)
    with open(filename, mode) as file:
        file.write(str(data) + "\n")


def append_to_file(data, filename):
    write_to_file(data, filename, "a")


def create_and_append_to_file(data, filename):
    write_to_file(data, filename, "w")


def parse_json(json_to_parse):
    try:
        print(json_to_parse)
        return json.loads(json_to_parse)
    except json.JSONDecodeError:
        print("Invalid JSON argument.")
        sys.exit(1)


# Check if a command-line argument is provided
if len(sys.argv) < 2:
    sys.exit(1)

# Get the JSON argument from the command-line
arguments_string = sys.argv[1]
arguments_json = parse_json(arguments_string)

task_result = multiply_arguments(arguments_json)

task_result_json = {"results": [str(task_result)]}
create_and_dump_json_to_file(task_result_json, path_to_task_results_file)

file_to_append_workflow_result = arguments_json["constants"][0]

if arguments_json["currentIteration"] == 0:
    create_and_append_to_file(task_result, path_to_work_directory + "/" + str(file_to_append_workflow_result))
else:
    append_to_file(task_result, path_to_work_directory + "/" + str(file_to_append_workflow_result))
