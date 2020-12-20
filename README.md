[![Scala Steward
badge](https://img.shields.io/badge/Scala_Steward-helping-blue.svg?style=flat&logo=data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAA4AAAAQCAMAAAARSr4IAAAAVFBMVEUAAACHjojlOy5NWlrKzcYRKjGFjIbp293YycuLa3pYY2LSqql4f3pCUFTgSjNodYRmcXUsPD/NTTbjRS+2jomhgnzNc223cGvZS0HaSD0XLjbaSjElhIr+AAAAAXRSTlMAQObYZgAAAHlJREFUCNdNyosOwyAIhWHAQS1Vt7a77/3fcxxdmv0xwmckutAR1nkm4ggbyEcg/wWmlGLDAA3oL50xi6fk5ffZ3E2E3QfZDCcCN2YtbEWZt+Drc6u6rlqv7Uk0LdKqqr5rk2UCRXOk0vmQKGfc94nOJyQjouF9H/wCc9gECEYfONoAAAAASUVORK5CYII=)](https://scala-steward.org)

### Using GH CLI to create repo from template:

```bash
gh repo create --template="git@github.com:robbyki/scala-template.git" "my-awesome-project"
```

Here is the alias I use to make the above even quicker (I use private by default):
```bash
gh alias set gct 'repo create --template="git@github.com:robbyki/scala-template.git" "$1" --private'
```

Which now makes the command as simple as:

`gh gct robbyki/my-awesome-project -y`

You have to then remember to pull the files from your new repo until GH team adds this part by default (https://github.com/cli/cli/issues/2290). 

`git pull origin master`

