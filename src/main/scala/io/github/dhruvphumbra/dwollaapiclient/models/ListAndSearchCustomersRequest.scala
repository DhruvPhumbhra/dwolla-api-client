package io.github.dhruvphumbra.dwollaapiclient.models

case class ListAndSearchCustomersRequest(
                                          limit: Option[Int] = None,
                                          offset: Option[Int] = None,
                                          search: Option[String] = None,
                                          email: Option[String] = None,
                                          status: Option[String] = None
                                        )
