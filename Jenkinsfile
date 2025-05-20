pipeline {
    agent any

    environment {
        DOCKERHUB_IMAGE = "keyssong/administracao"
        IMAGE_TAG = "latest"
        DEPLOYMENT_FILE = "k8s/administracao-deployment.yaml"
    }

    triggers {
        pollSCM('H/5 * * * *') // Poll a cada 5 minutos
    }

    options {
        disableConcurrentBuilds()
    }

    stages {
        stage('Verificar Branch') {
            when {
                branch 'master'
            }
            steps {
                echo "Executando pipeline na branch master"
            }
        }

        stage('Checkout do Código') {
            steps {
                git credentialsId: 'Github',
                     url: 'https://github.com/KeyssonG/api-administracao.git ',
                     branch: 'master'
            }
        }

        stage('Push da Imagem para Docker Hub') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'dockerhub', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                    bat """
                        docker login -u %DOCKER_USER% -p %DOCKER_PASS%
                        docker push %DOCKERHUB_IMAGE%:%IMAGE_TAG%
                        docker push %DOCKERHUB_IMAGE%:latest
                    """
                }
            }
        }

        stage('Atualizar deployment.yaml') {
            steps {
                script {
                    def commitSuccess = false

                    // Substituir a imagem no deployment.yaml
                    bat """
                        powershell -Command "\$content = Get-Content '${DEPLOYMENT_FILE}'; \$newContent = \$content -replace 'image: .*', 'image: ${DOCKERHUB_IMAGE}:${IMAGE_TAG}'; if (-not (\$content -eq \$newContent)) { \$newContent | Set-Content '${DEPLOYMENT_FILE}' }"
                    """

                    // Adicionar e commitar o arquivo YAML se houver alteração
                    bat """
                        git config user.email "jenkins@pipeline.com"
                        git config user.name "Jenkins"
                        git add "${DEPLOYMENT_FILE}"
                        git diff --cached --quiet || git commit -m "Atualiza imagem Docker para latest"
                    """

                    // Verificar se houve commit
                    commitSuccess = bat(script: 'git diff --cached --quiet || echo "changed"', returnStdout: true).trim() == "changed"

                    if (commitSuccess) {
                        echo "Alterações no arquivo de deployment detectadas. Commit realizado."
                    } else {
                        echo "Nenhuma alteração detectada no arquivo de deployment. Não foi realizado commit."
                    }
                }
            }
        }

        stage('Aplicar Alterações no Cluster Kubernetes') {
            steps {
                // Aplicar o deployment atualizado
                bat "kubectl apply -f ${DEPLOYMENT_FILE}"
            }
        }
    }

    post {
        success {
            echo "Pipeline concluída com sucesso! A imagem '${DOCKERHUB_IMAGE}:latest' foi aplicada no cluster Kubernetes. 🚀"
        }
        failure {
            echo "Erro na pipeline. Confira os logs para mais detalhes."
        }
    }
}